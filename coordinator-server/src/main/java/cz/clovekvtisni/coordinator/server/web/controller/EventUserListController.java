package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.domain.RegistrationStatus;
import cz.clovekvtisni.coordinator.server.domain.*;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserGroupService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.model.FilterParams;
import cz.clovekvtisni.coordinator.server.web.model.SelectedUserAction;
import cz.clovekvtisni.coordinator.server.web.model.UserMultiSelection;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import cz.clovekvtisni.coordinator.util.ValueTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
@Controller
@RequestMapping("/admin/event/user/list")
public class EventUserListController extends AbstractEventController {

    @Autowired
    private UserInEventService userInEventService;

    @Autowired
    private UserGroupService userGroupService;

    @RequestMapping(method = RequestMethod.GET)
    public String listUsers(@ModelAttribute("params") EventFilterParams params, Model model) {
        EventEntity event = loadEventById(params.getEventId());
        model.addAttribute("event", event);

        UserInEventFilter inEventFilter = createFilterFromParams(params);
        ResultList<UserInEventEntity> userInEvents = userInEventService.findByFilter(inEventFilter, 0, null, UserInEventService.FLAG_FETCH_USER | UserInEventService.FLAG_FETCH_GROUPS);
        model.addAttribute("userInEvents", userInEvents.getResult());

        UserMultiSelection selectionForm = new UserMultiSelection();
        selectionForm.setEventId(params.getEventId());
        model.addAttribute("selectionForm", selectionForm);

        PoiFilter poiFilter = new PoiFilter();
        poiFilter.setEventIdVal(params.getEventId());
        poiFilter.setWorkflowIdVal(0l);
        poiFilter.setWorkflowIdOp(Filter.Operator.NOT_EQ);
        model.addAttribute("tasks", poiService.findByFilter(poiFilter, 0, null, 0l).getResult());

        model.addAttribute("userGroups", userGroupService.findByEventId(event.getId(), 0l));

        populateEventModel(model, params);

        return "admin/event-users";
    }

    private UserInEventFilter createFilterFromParams(EventFilterParams params) {
        UserInEventFilter inEventFilter = new UserInEventFilter();
        inEventFilter.setEventIdVal(params.getEventId());
        if (!ValueTool.isEmpty(params.getUserFulltext())) {
            final String fullText = params.getUserFulltext().trim().toLowerCase();
            inEventFilter.addAfterLoadCallback(new Filter.AfterLoadCallback<UserInEventEntity>() {
                @Override
                public boolean accept(UserInEventEntity entity) {
                    UserEntity user = entity.getUserEntity();
                    return user != null && user.getFullName() != null && user.getFullName().toLowerCase().contains(fullText);
                }
            });
        }
        final Long groupId = params.getGroupId();
        if (groupId != null) {
            inEventFilter.addAfterLoadCallback(new Filter.AfterLoadCallback<UserInEventEntity>() {
                @Override
                public boolean accept(UserInEventEntity entity) {
                    return entity.getGroupIdList() != null &&
                            Arrays.asList(entity.getGroupIdList()).contains(groupId);
                }
            });
        }
        return inEventFilter;
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

                case REGISTER_TO_TASK:
                    Long placeId = selection.getSelectedTaskId();
                    if (placeId != null) {
                        PoiEntity place = poiService.findById(placeId, 0l);
                        Set<Long> updateList = new HashSet<Long>();
                        Long[] registered = place.getUserIdList();
                        if (registered != null)
                            updateList.addAll(Arrays.asList(registered));
                        for (Long userId : userIds)
                            updateList.add(userId);
                        place.setUserIdList(updateList.toArray(new Long[0]));
                        poiService.updatePoi(place);
                    }
                    break;

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

    public static Breadcrumb getBreadcrumb(FilterParams params) {
        return new Breadcrumb(params, "/admin/event/user/list", "breadcrumb.eventUsers");
    }

    @ModelAttribute("selectedUserActions")
    public SelectedUserAction[] selectedUserActions() {
        return SelectedUserAction.values();
    }
}
