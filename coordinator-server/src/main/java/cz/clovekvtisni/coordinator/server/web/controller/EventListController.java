package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.EventFilter;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping("/admin/event/list")
public class EventListController extends AbstractController {

    @Autowired
    private EventService eventService;

    @RequestMapping
    public String list(@RequestParam(value = "bookmark", required = false) String bookmark, Model model) {

        UserEntity loggedUser = getLoggedUser();
        ResultList<EventEntity> events;
        if (isSuperAdmin(loggedUser)) {
            events = eventService.findByFilter(new EventFilter(), DEFAULT_LIST_LENGTH, bookmark, EventService.FLAG_FETCH_LOCATIONS);

        } else {
            OrganizationInEventFilter inEventFilter = new OrganizationInEventFilter();
            inEventFilter.setOrganizationIdVal(loggedUser.getOrganizationId());
            events = eventService.findByOrganizationFilter(inEventFilter, DEFAULT_LIST_LENGTH, bookmark, EventService.FLAG_FETCH_LOCATIONS);
        }

        model.addAttribute("events", events.getResult());

        return "admin/event-list";
    }

    public static Breadcrumb getBreadcrumb() {
        return new Breadcrumb("/admin/event/list", "breadcrumb.eventList");
    }

    @ModelAttribute("breadcrumbs")
    public Breadcrumb[] breadcrumbs() {
        return new Breadcrumb[] {
                UserListController.getBreadcrumb(),
                EventListController.getBreadcrumb()
        };
    }
}
