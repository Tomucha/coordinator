package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserGroupService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.model.*;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import cz.clovekvtisni.coordinator.util.ValueTool;
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

    @Autowired
    private UserGroupService userGroupService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(@ModelAttribute("params") EventFilterParams params, @RequestParam(value = "bookmark", required = false) String bookmark, Model model) {
        PoiFilter filter = new PoiFilter();
        filter.setEventIdVal(params.getEventId());
        ResultList<PoiEntity> result = poiService.findByFilter(filter, DEFAULT_LIST_LENGTH, bookmark, 0l);

        model.addAttribute("placeList", result.getResult());

        PoiMultiSelection selectionForm = new PoiMultiSelection();
        selectionForm.setEventId(appContext.getActiveEvent().getId());
        model.addAttribute("selectionForm", selectionForm);

        model.addAttribute("userGroups", userGroupService.findByEventId(appContext.getActiveEvent().getId(), 0l));

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

    @RequestMapping(method = RequestMethod.POST, value = "/change-workflow-state")
    public String onChangeWorkflowState(@ModelAttribute @Valid ChangeWorkflowStateForm form, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            PoiEntity poi = poiService.findById(form.getPlaceId(), 0l);
            if (poi != null) {
                if (ValueTool.isEmpty(form.getTransitionId())) {
                    poiService.startWorkflow(poi);
                } else {
                    poiService.transitWorkflowState(poi, form.getTransitionId());
                }
            }
        }
        return "redirect: /admin/event/place/list?eventId=" + form.getEventId();

    }

    public static Breadcrumb getBreadcrumb(EventEntity params) {
        return new Breadcrumb(params, "/admin/event/place/list", "breadcrumb.eventPlaces");
    }

    @ModelAttribute("selectedPoiActions")
    public SelectedPoiAction[] selectedPoiActions() {
        return SelectedPoiAction.values();
    }
}
