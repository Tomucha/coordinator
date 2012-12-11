package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.model.FilterParams;
import cz.clovekvtisni.coordinator.server.web.model.UserMultiSelection;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
@Controller
@RequestMapping("/admin/event/users")
public class EventUserListController extends AbstractEventController {

    @Autowired
    private UserInEventService userInEventService;

    @Autowired
    private PoiService poiService;

    @RequestMapping(method = RequestMethod.GET)
    public String listUsers(@ModelAttribute("params") EventFilterParams params, Model model) {
        if (params.getEventId() == null)
            throw NotFoundException.idNotExist();

        UserInEventFilter inEventFilter = new UserInEventFilter();
        inEventFilter.setEventIdVal(params.getEventId());
        if (params.getUserFulltext() != null) {
            final String fullText = params.getUserFulltext();
            inEventFilter.addAfterLoadCallback(new Filter.AfterLoadCallback<UserInEventEntity>() {
                @Override
                public boolean accept(UserInEventEntity entity) {
                    UserEntity user = entity.getUserEntity();
                    return user.getFullName() != null && user.getFullName().contains(fullText);
                }
            });
        }
        ResultList<UserInEventEntity> userInEvents = userInEventService.findByFilter(inEventFilter, 0, null, UserInEventService.FLAG_FETCH_USER);
        model.addAttribute("userInEvents", userInEvents.getResult());

        UserMultiSelection selectionForm = new UserMultiSelection();
        selectionForm.setEventId(params.getEventId());
        model.addAttribute("selectionForm", selectionForm);

        PoiFilter poiFilter = new PoiFilter();
        poiFilter.setEventIdVal(params.getEventId());
        poiFilter.setWorkflowIdVal(0l);
        poiFilter.setWorkflowIdOp(Filter.Operator.NOT_EQ);
        model.addAttribute("tasks", poiService.findByFilter(poiFilter, 0, null, PoiService.FLAG_FETCH_FROM_CONFIG));

        populateEventModel(model, params);

        return "admin/event-users";
    }
    @RequestMapping(method = RequestMethod.POST, params = "selectedUsers")
    public String onSelectedAction(@ModelAttribute UserMultiSelection selection) {
        if (selection.getSelectedUsers() != null && selection.getSelectedUsers().length > 0)  {
            String selectedAction = selection.getSelectedAction();
            Long selectedPlaceId = selection.getSelectedTaskId();

            // TODO
            if (selectedAction.equals("delete")) {

            } else if (selectedAction.equals("suspend")) {

            } else if (selectedAction.equals("registerToTask") && selectedPlaceId != null) {
                PoiEntity place = poiService.findById(selectedPlaceId, 0l);
                Set<Long> updateList = new HashSet<Long>();
                Long[] registered = place.getUserId();
                if (registered != null)
                    updateList.addAll(Arrays.asList(registered));
                for (Long userId : selection.getSelectedUsers())
                    updateList.add(userId);
                place.setUserId(updateList.toArray(new Long[0]));
                poiService.updatePoi(place);
            }
        }

        return "redirect:/admin/event/users?eventId=" + selection.getEventId();
    }

    public static Breadcrumb getBreadcrumb(FilterParams params) {
        return new Breadcrumb(params, "/admin/event/users", "breadcrumb.eventUsers");
    }
}
