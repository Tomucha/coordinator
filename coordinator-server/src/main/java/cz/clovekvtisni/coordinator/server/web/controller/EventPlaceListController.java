package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.model.*;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
@Controller
@RequestMapping("/admin/event/place/list")
public class EventPlaceListController extends AbstractEventController {

    @Autowired
    private PoiService poiService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(@ModelAttribute("params") EventFilterParams params, @RequestParam(value = "bookmark", required = false) String bookmark, Model model) {
        EventEntity event = loadEventById(params.getEventId());
        model.addAttribute("event", event);

        PoiFilter filter = new PoiFilter();
        filter.setEventIdVal(params.getEventId());
        ResultList<PoiEntity> result = poiService.findByFilter(filter, DEFAULT_LIST_LENGTH, bookmark, 0l);

        model.addAttribute("placeList", result.getResult());
        populateEventModel(model, params);

        PoiMultiSelection selectionForm = new PoiMultiSelection();
        selectionForm.setEventId(event.getId());
        model.addAttribute("selectionForm", selectionForm);

        return "admin/event-places";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSelectedAction(@ModelAttribute("selectionForm") @Valid PoiMultiSelection selection, BindingResult bindingResult) {
        List<Long> pois = selection.getSelectedPois();
        SelectedPoiAction action = selection.getSelectedAction();

        if (action != null && pois != null && pois.size() > 0) {
            for (Long poiId : pois) {
                PoiEntity poiEntity = poiService.findById(poiId, 0l);
                if (poiEntity == null)
                    continue;

                switch (action) {
                    case DELETE:
                        poiService.deletePoi(poiEntity, 0l);
                        break;
                }
            }
        }

        return "redirect: /admin/event/place/list?eventId=" + selection.getEventId();
    }

    public static Breadcrumb getBreadcrumb(FilterParams params) {
        return new Breadcrumb(params, "/admin/event/place/list", "breadcrumb.eventPlaces");
    }

    @ModelAttribute("selectedPoiActions")
    public SelectedPoiAction[] selectedPoiActions() {
        return SelectedPoiAction.values();
    }
}
