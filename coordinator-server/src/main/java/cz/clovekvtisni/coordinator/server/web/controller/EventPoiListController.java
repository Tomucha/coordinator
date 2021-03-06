package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.domain.config.RolePermission;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserGroupService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.EventPrerequisitiesRequired;
import cz.clovekvtisni.coordinator.server.web.model.*;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import cz.clovekvtisni.coordinator.util.ValueTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/event/poi/list")
@EventPrerequisitiesRequired
public class EventPoiListController extends AbstractEventController {

    @Autowired
    private PoiService poiService;

    @Autowired
    private UserGroupService userGroupService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(@ModelAttribute("poiListFilter") EventFilterParams params, @RequestParam(value = "bookmark", required = false) String bookmark, Model model, HttpSession session) {

	    String filterSessionKey = "poiListFilter"+params.getEventId();

        boolean sentByUser = params.isSentByUser();
        if (!sentByUser && session.getAttribute(filterSessionKey)!=null) {
            params = (EventFilterParams) session.getAttribute(filterSessionKey);
        }

        PoiFilter filter = new PoiFilter();
        params.populatePoiFilter(filter);
        UserEntity loggedUser = getLoggedUser();
        if (!authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_POI_IN_ORG))
            filter.setVisibleForRolesVal(loggedUser.getRoleIdList());

        ResultList<PoiEntity> result = poiService.findByFilter(filter, DEFAULT_LIST_LENGTH, bookmark, 0l);

        model.addAttribute("poiList", result.getResult());

        PoiMultiSelection selectionForm = new PoiMultiSelection();
        selectionForm.setEventId(appContext.getActiveEvent().getId());
        model.addAttribute("selectionForm", selectionForm);

        model.addAttribute("userGroups", userGroupService.findByEventId(appContext.getActiveEvent().getId(), 0l));

        session.setAttribute(filterSessionKey, params);
        if (!sentByUser) {
            model.addAttribute("poiListFilter", params);
        }

        return "admin/event-pois";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSelectedAction(@ModelAttribute("selectionForm") @Valid PoiMultiSelection selection, BindingResult bindingResult) {
        List<Long> pois = selection.getSelectedPois();
        SelectedPoiAction action = selection.getSelectedAction();

        if (action != null && pois != null && pois.size() > 0) {
            for (Long poiId : pois) {
                if (poiId == null)
                    continue;
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

        return "redirect:/admin/event/poi/list?eventId=" + selection.getEventId();
    }

    @ModelAttribute("selectedPoiActions")
    public SelectedPoiAction[] selectedPoiActions() {
        return SelectedPoiAction.values();
    }
}
