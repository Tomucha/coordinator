package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/event/list")
public class EventListController extends AbstractController {

    @Autowired
    private EventService eventService;

    @RequestMapping
    public String list(@RequestParam(value = "bookmark", required = false) String bookmark, Model model) {
        UserEntity loggedUser = getLoggedUser();
        // TODO - az bude mechanismus opravneni: ResultList<EventEntity> events = eventService.findByOrganization(loggedUser.getOrganizationId(), 30, bookmark, EventService.FLAG_FETCH_LOCATIONS);
        ResultList<EventEntity> events = eventService.findByFilter(new EventFilter(), 100, null, 0l);

        model.addAttribute("events", events.getResult());

        return "admin/event-list";
    }

    @ModelAttribute("lastPoiList")
    public List<PoiEntity> lastPoiList() {
        /*
        PoiFilter filter = new PoiFilter();
        filter.setOrder("createdDate");
        ResultList<PoiEntity> result = poiService.findByFilter(filter, 30, null, 0l);

        return result.getResult();
        */

        List<PoiEntity> pois = new ArrayList<PoiEntity>(3);

        Calendar cal = Calendar.getInstance();
        PoiEntity poi1 = new PoiEntity();
        poi1.setLatitude(50.0339164);
        poi1.setLongitude(14.5563036);
        poi1.setCreatedDate(cal.getTime());
        pois.add(poi1);

        cal.add(Calendar.HOUR_OF_DAY, -3);
        PoiEntity poi2 = new PoiEntity();
        poi2.setLatitude(50.0339164);
        poi2.setLongitude(14.5563036);
        poi2.setCreatedDate(cal.getTime());
        pois.add(poi2);

        cal.add(Calendar.DAY_OF_MONTH, -3);
        PoiEntity poi3 = new PoiEntity();
        poi3.setLatitude(50.0339164);
        poi3.setLongitude(14.5563036);
        poi3.setCreatedDate(cal.getTime());
        pois.add(poi3);

        return pois;
    }
}
