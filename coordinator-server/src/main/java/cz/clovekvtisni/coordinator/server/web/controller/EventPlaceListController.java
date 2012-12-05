package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.service.PoiService;
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
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
@Controller
@RequestMapping("/admin/event/places")
public class EventPlaceListController extends AbstractEventController {

    @Autowired
    private PoiService poiService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(@ModelAttribute("params") EventFilterParams params, @RequestParam(value = "bookmark", required = false) String bookmark, Model model) {
        if (params.getEventId() == null)
            throw NotFoundException.idNotExist();

        PoiFilter filter = new PoiFilter();
        filter.setEventIdVal(params.getEventId());
        ResultList<PoiEntity> result = poiService.findByFilter(filter, DEFAULT_LIST_LENGTH, bookmark, PoiService.FLAG_FETCH_FROM_CONFIG);

        model.addAttribute("placeList", result.getResult());
        populateEventModel(model, params);

        return "admin/event-places";
    }

    public static Breadcrumb getBreadcrumb(FilterParams params) {
        return new Breadcrumb(params, "/admin/event/places", "breadcrumb.eventPlaces");
    }

}
