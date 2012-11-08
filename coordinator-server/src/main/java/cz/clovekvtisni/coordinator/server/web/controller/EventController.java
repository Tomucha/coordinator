package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.filter.EventFilter;
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
@RequestMapping(value = "/admin/event")
public class EventController extends AbstractController {

    @Autowired
    private EventService eventService;

    @RequestMapping
    public String list(@RequestParam(value = "bookmark", required = false) String bookmark, Model model) {
        EventFilter filter = new EventFilter();
        ResultList<EventEntity> events = eventService.findByFilter(filter, 30, bookmark);

        model.addAttribute("events", events);

        return "admin/event-list";
    }
}
