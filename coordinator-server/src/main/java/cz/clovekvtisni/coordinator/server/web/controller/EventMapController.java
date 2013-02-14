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
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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

    @Autowired
    private ObjectMapper objectMapper;


    @RequestMapping(method = RequestMethod.GET)
    public String map(@ModelAttribute("params") EventFilterParams params, Model model) {
        if (params.getEventId() == null)
            throw NotFoundException.idNotExist();


/*
        EventEntity event = loadEventById(params.getEventKey());
        model.addAttribute("event", event);
*/

        //populateEventModel(model, params);

        UserInEventFilter userInEventFilter = new UserInEventFilter();
        userInEventFilter.setEventIdVal(appContext.getActiveEvent().getId());
        ResultList<UserInEventEntity> inEvents = userInEventService.findByFilter(userInEventFilter, 0, null, UserInEventService.FLAG_FETCH_USER);
        model.addAttribute("userInEventList", inEvents.getResult());

        PoiFilter poiFilter = new PoiFilter();
        poiFilter.setEventIdVal(appContext.getActiveEvent().getId());
        ResultList<PoiEntity> pois = poiService.findByFilter(poiFilter, 0, null, 0l);
        model.addAttribute("poiList", pois.getResult());

        return "admin/event-map";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/poi")
    public @ResponseBody List<PoiEntity> listPoi(
            @RequestParam(required = true) long eventId,
            @RequestParam(required = true) double latN,
            @RequestParam(required = true) double lonE,
            @RequestParam(required = true) double latS,
            @RequestParam(required = true) double lonW
    ) {
        return poiService.findByEventAndBox(eventId, latN, lonE, latS, lonW, 0);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/popup/poi")
    public String popup(
            @RequestParam(value = "eventId", required = false) Long eventId,
            @RequestParam(value = "poiId", required = false) Long poiId,
            Model model) {

        PoiEntity e = poiService.findById(poiId, 0);
        model.addAttribute("poi", e);

        Set<Long> userIds = e.getUserIdList();
        List<UserInEventEntity> assignedUsers = userInEventService.findByIds(e.getEventId(), userIds, UserInEventService.FLAG_FETCH_USER);
        model.addAttribute("assignedUsers", assignedUsers);

        return "ajax/poi-popup";
    }


    public static Breadcrumb getBreadcrumb(EventEntity params) {
        return new Breadcrumb(params, "/admin/event/map", "breadcrumb.eventMap");
    }

}
