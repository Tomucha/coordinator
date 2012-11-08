package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.EventFilter;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping(value = "/admin/event/list")
public class EventListController extends AbstractController {

    @Autowired
    private EventService eventService;

    @RequestMapping
    public String list(@RequestParam(value = "bookmark", required = false) String bookmark, Model model) {
        UserEntity loggedUser = getLoggedUser();
        ResultList<EventEntity> events = eventService.findByOrganization(loggedUser.getOrganizationId(), 30, bookmark, EventService.FLAG_FETCH_LOCATIONS);

        model.addAttribute("events", events);

        return "admin/event-list";
    }
}
