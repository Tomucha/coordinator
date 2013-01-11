package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.model.FilterParams;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
@Controller
@RequestMapping("/admin/event/map")
public class EventMapController extends AbstractEventController {

    @Autowired
    private UserInEventService userInEventService;

    @Autowired
    private PoiService poiService;

    @RequestMapping(method = RequestMethod.GET)
    public String map(@ModelAttribute("params") EventFilterParams params, Model model) {
        if (params.getEventId() == null)
            throw NotFoundException.idNotExist();
        EventEntity event = loadEventById(params.getEventId());
        model.addAttribute("event", event);
        populateEventModel(model, params);

        UserInEventFilter userInEventFilter = new UserInEventFilter();
        userInEventFilter.setEventIdVal(event.getId());
        ResultList<UserInEventEntity> inEvents = userInEventService.findByFilter(userInEventFilter, 0, null, UserInEventService.FLAG_FETCH_USER);
        model.addAttribute("userInEventList", inEvents.getResult());

        PoiFilter poiFilter = new PoiFilter();
        poiFilter.setEventIdVal(event.getId());
        ResultList<PoiEntity> places = poiService.findByFilter(poiFilter, 0, null, 0l);
        model.addAttribute("placeList", places.getResult());

        return "admin/event-map";
    }

    public static Breadcrumb getBreadcrumb(FilterParams params) {
        return new Breadcrumb(params, "/admin/event/map", "breadcrumb.eventMap");
    }

}
