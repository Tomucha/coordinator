package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.model.FilterParams;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
@Controller
@RequestMapping("/admin/event/places")
public class EventPlacesController extends AbstractEventController {

    @RequestMapping(method = RequestMethod.GET)
    public String list(@ModelAttribute("params") EventFilterParams params, Model model) {
        if (params.getEventId() == null)
            throw NotFoundException.idNotExist();

        EventEntity event = getEventById(params.getEventId());
        populateEventModel(model, new EventFilterParams(event));

        return "admin/event-places";
    }

    public static Breadcrumb getBreadcrumb(FilterParams params) {
        return new Breadcrumb(params, "/admin/event/places", "breadcrumb.eventPlaces");
    }

}
