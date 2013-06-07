package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.domain.RegistrationStatus;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserGroupEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.service.UserGroupService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.EventPrerequisitiesRequired;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.model.SelectedUserAction;
import cz.clovekvtisni.coordinator.server.web.model.UserMultiSelection;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
@Controller
@RequestMapping("/admin/event/user/list")
@EventPrerequisitiesRequired
public class EventUserListController extends AbstractEventController {

    @Autowired
    private UserInEventService userInEventService;

    @Autowired
    private UserGroupService userGroupService;

    @RequestMapping(method = RequestMethod.GET)
    public String listUsers(@ModelAttribute("params") EventFilterParams params, Model model) {

        UserInEventFilter inEventFilter = new UserInEventFilter();
        params.populateUserInEventFilter(inEventFilter);

        ResultList<UserInEventEntity> userInEvents = userInEventService.findByFilter(inEventFilter, 0, null,
                UserInEventService.FLAG_FETCH_GROUPS | UserInEventService.FLAG_FETCH_LAST_POI
        );
        model.addAttribute("userInEvents", userInEvents.getResult());

        UserMultiSelection selectionForm = new UserMultiSelection();
        selectionForm.setEventId(params.getEventId());
        model.addAttribute("selectionForm", selectionForm);

        PoiFilter poiFilter = new PoiFilter();
        poiFilter.setEventIdVal(params.getEventId());

        model.addAttribute("userGroups", userGroupService.findByEventId(appContext.getActiveEvent().getId(), 0l));

        return "admin/event-users";
    }

    @RequestMapping(method = RequestMethod.GET, params = "ajax")
    public String listUsersAjax(@ModelAttribute("params") EventFilterParams params, Model model) {

        UserInEventFilter filter = new UserInEventFilter();
        params.populateUserInEventFilter(filter);
        ResultList<UserInEventEntity> userInEvents = userInEventService.findByFilter(filter, 0, null,
                UserInEventService.FLAG_FETCH_GROUPS | UserInEventService.FLAG_FETCH_LAST_POI
        );
        model.addAttribute("userInEvents", userInEvents.getResult());
        model.addAttribute("userGroups", userGroupService.findByEventId(appContext.getActiveEvent().getId(), 0l));

        return "ajax/event-users";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSelectedAction(@ModelAttribute("selectionForm") @Valid UserMultiSelection selection, BindingResult bindingResult) {
        List<Long> userIds = selection.getSelectedUsers();
        SelectedUserAction action = selection.getSelectedAction();
        if (userIds != null && userIds.size() > 0 && action != null)  {

            switch (action) {
                case EXPEL:
                case CONFIRM:
                    UserInEventFilter filter = new UserInEventFilter();
                    filter.setEventIdVal(selection.getEventId());
                    ResultList<UserInEventEntity> inEventEntities = userInEventService.findByFilter(filter, 0, null, 0l);
                    RegistrationStatus status = action == SelectedUserAction.CONFIRM ? RegistrationStatus.CONFIRMED : RegistrationStatus.EXPELLED;
                    for (UserInEventEntity inEventEntity : inEventEntities) {
                        if (userIds.contains(inEventEntity.getUserId()))
                            userInEventService.changeStatus(inEventEntity, status);
                    }
                    break;

                case SUSPEND:
                    for (Long userId : userIds) {
                        if (userId != null)
                            userService.suspendUser(userId, selection.getSuspendReason(), 0l);
                    }
                    break;

/*
                FIXME: takhle ten assign nemuze byt, delam ho jinak

                case REGISTER_TO_TASK:
                    Long poiId = selection.getSelectedTaskId();
                    if (poiId != null) {
                        PoiEntity poi = poiService.findById(poiId, 0l);
                        Set<Long> updateList = new HashSet<Long>();
                        Long[] registered = poi.getUserIdList();
                        if (registered != null)
                            updateList.addAll(Arrays.asList(registered));
                        for (Long userId : userIds)
                            if (userId != null)
                                updateList.add(userId);
                        poi.setUserIdList(updateList.toArray(new Long[0]));
                        poiService.updatePoi(poi);
                    }
                    break;
*/

                case ADD_TO_GROUP:
                    Long groupId = selection.getGroupId();
                    if (groupId != null) {
                        UserGroupEntity userGroup = userGroupService.findById(groupId, 0l);
                        if (userGroup != null) {
                            userGroupService.addUsersToGroup(userGroup, userIds.toArray(new Long[0]));
                        }
                    }
                    break;
            }
        }

        return "redirect:/admin/event/user/list?eventId=" + selection.getEventId();
    }

    @ModelAttribute("selectedUserActions")
    public SelectedUserAction[] selectedUserActions() {
        return SelectedUserAction.values();
    }
}
