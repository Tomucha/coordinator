package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.12.12                                                                                                Ò
 */
@Controller
@RequestMapping("/admin/event/place/workflow")
public class EventPlaceWorkflowController extends AbstractEventController {

    @Autowired
    private PoiService poiService;

    @Autowired
    private UserInEventService userInEventService;

    @RequestMapping(method = RequestMethod.GET)
    public String edit(
            @RequestParam(value = "eventId", required = true) Long eventId,
            @RequestParam(value = "poiId", required = true) Long poiId,
            Model model) {


        PoiEntity poi = poiService.findById(poiId, 0);
        model.addAttribute("poi", poi);
        return "admin/event-poi-workflow";
    }


    @RequestMapping(method = RequestMethod.GET)
    public String changeWorkflowState(
            @RequestParam(value = "eventId", required = true) Long eventId,
            @RequestParam(value = "poiId", required = true) Long poiId,
            @RequestParam(value = "transitionId", required = true) String transitionId,
            Model model,
            BindingResult bindingResult) {
        PoiEntity poi = poiService.findById(poiId, 0l);
        poiService.transitWorkflowState(poi, transitionId);
        return "redirect: /admin/event/place/list?eventId=" + eventId;

    }

}
