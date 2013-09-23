package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserGroupService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.web.EventPrerequisitiesRequired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.12.12                                                                                                Ò
 */
@Controller
@RequestMapping("/admin/event/poi/workflow")
@EventPrerequisitiesRequired
public class EventPoiWorkflowController extends AbstractEventController {

    @Autowired
    private PoiService poiService;

    @Autowired
    private UserGroupService userGroupService;

    @RequestMapping(method = RequestMethod.GET)
    public String edit(
            @RequestParam(value = "eventId", required = true) Long eventId,
            @RequestParam(value = "poiId", required = true) Long poiId,
            Model model) {

        PoiEntity poi = poiService.findById(poiId, 0);
        model.addAttribute("poi", poi);
        model.addAttribute("userGroups", userGroupService.findByEventId(appContext.getActiveEvent().getId(), 0l));
        return "admin/event-poi-workflow";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/transition")
    public String changeWorkflowState(
            @RequestParam(value = "eventId", required = true) Long eventId,
            @RequestParam(value = "poiId", required = true) Long poiId,
            @RequestParam(value = "transitionId", required = true) String transitionId,
            @RequestParam(value = "comment", required = false) String comment,
            Model model) {
        PoiEntity poi = poiService.findById(poiId, 0l);

        String goingToState = poi.getWorkflowState().getTransitionMap().get(transitionId).getToStateId();
        if (poi.getWorkflow().getStateMap().get(goingToState).isRequiresAssignment()) {
            // new state requires assigned users
            if (poi.getUserIdList().isEmpty())  {
                setGlobalMessage(getMessage("label.missingAssignee"), model);
                return edit(eventId, poiId, model);
            }
        }

        poiService.transitWorkflowState(poi, transitionId, comment, PoiService.FLAG_DISABLE_FORCE_SINGLE_ASSIGN);
        return "redirect:/admin/event/poi/list?eventId=" + eventId;

    }

}
