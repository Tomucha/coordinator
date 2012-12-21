package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.domain.EventEntity;
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

    @RequestMapping(method = RequestMethod.GET)
    public String listUsers(@ModelAttribute("params") EventFilterParams params, Model model) {
        EventEntity event = loadEventById(params.getEventId());
        model.addAttribute("event", event);

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
        ResultList<UserInEventEntity> userInEvents = userInEventService.findByFilter(inEventFilter, 0, null, UserInEventService.FLAG_FETCH_USER);
        model.addAttribute("userInEvents", userInEvents.getResult());

        UserMultiSelection selectionForm = new UserMultiSelection();
        selectionForm.setEventId(params.getEventId());
        model.addAttribute("selectionForm", selectionForm);

        PoiFilter poiFilter = new PoiFilter();
        poiFilter.setEventIdVal(params.getEventId());
        poiFilter.setWorkflowIdVal(0l);
        poiFilter.setWorkflowIdOp(Filter.Operator.NOT_EQ);
        model.addAttribute("tasks", poiService.findByFilter(poiFilter, 0, null, PoiService.FLAG_FETCH_FROM_CONFIG).getResult());

        populateEventModel(model, params);

        return "admin/event-users";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSelectedAction(@ModelAttribute("selectionForm") @Valid UserMultiSelection selection, BindingResult bindingResult) {
        if (selection.getSelectedUsers() != null && selection.getSelectedUsers().size() > 0)  {
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

        return "redirect:/admin/event/user/list?eventId=" + selection.getEventId();
    }

    public static Breadcrumb getBreadcrumb(FilterParams params) {
        return new Breadcrumb(params, "/admin/event/user/list", "breadcrumb.eventUsers");
    }
}
