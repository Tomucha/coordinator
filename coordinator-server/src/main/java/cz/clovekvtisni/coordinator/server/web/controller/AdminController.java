package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.model.FilterParams;
import cz.clovekvtisni.coordinator.server.web.model.UserMultiSelection;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin")
public class AdminController extends AbstractEventController {

    @RequestMapping
    public String show(@ModelAttribute("params") EventFilterParams params, Model model) {
        EventEntity event = loadEventById(params.getEventId());
        model.addAttribute("event", event);
        populateEventModel(model, params);
        return "admin/home";
    }

    public static Breadcrumb getBreadcrumb(FilterParams params) {
        return new Breadcrumb(params, "/admin", "breadcrumb.admin");
    }

}
