package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController extends AbstractEventController {

    @RequestMapping
    public String show(@ModelAttribute("params") EventFilterParams params, Model model) {
        if (params.getEventId() != null)
            return "redirect:/admin/event/map?eventId="+params.getEventId();
        else
            return "redirect:/superadmin/event/list";
    }

    public static Breadcrumb getBreadcrumb(EventEntity event) {
        return new Breadcrumb(event, "/admin", "breadcrumb.admin");
    }

}
