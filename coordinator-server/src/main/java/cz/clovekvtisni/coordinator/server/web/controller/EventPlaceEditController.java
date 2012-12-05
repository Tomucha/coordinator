package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.12.12
 */
@Controller
@RequestMapping("/admin/event/place/edit")
public class EventPlaceEditController extends AbstractEventController {

    @Autowired
    private PoiService poiService;

    @RequestMapping(method = RequestMethod.GET)
    public String create(
            @ModelAttribute("params") EventFilterParams params,
            @RequestParam("eventId") Long eventId,
            Model model) {

        EventEntity event = getEventById(eventId);
        PoiEntity poi = new PoiEntity();
        model.addAttribute("form", poi);

        populateEventModel(model, new EventFilterParams(eventId));

        return "admin/event-place-edit";
    }
}
