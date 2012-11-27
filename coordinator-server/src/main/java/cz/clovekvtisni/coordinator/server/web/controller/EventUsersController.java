package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
@Controller
@RequestMapping("/admin/event/users")
public class EventUsersController extends AbstractEventController {

    @RequestMapping(method = RequestMethod.GET)
    public String listUsers(@RequestParam("id") Long eventId, Model model) {
        EventEntity event = getEventById(eventId);
        populateEventModel(model, event);

        return "admin/event-users";
    }

    public static Breadcrumb getBreadcrumb(EventEntity entity) {
        return new Breadcrumb(entity, "/admin/event/users", "breadcrumb.eventUsers");
    }
}
