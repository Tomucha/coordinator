package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.model.PoiForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.12.12
 */
@Controller
@RequestMapping("/admin/event/poi/edit")
public class EventPoiEditController extends AbstractEventController {

    @Autowired
    private PoiService poiService;

    @Autowired
    private UserInEventService userInEventService;

    @RequestMapping(method = RequestMethod.GET)
    public String edit(
            @ModelAttribute("params") EventFilterParams params,
            @RequestParam(value = "poiId", required = false) Long poiId,
            Model model) {

        PoiForm form;
        if (poiId == null) {
            UserEntity user = getLoggedUser();
            form = new PoiForm();
            form.setEventId(appContext.getActiveEvent().getId());
            form.setOrganizationId(user.getOrganizationId());
        } else {
            PoiEntity poiEntity = poiService.findById(poiId, 0l);
            params.setEventId(poiEntity.getEventId());
            if (poiEntity == null)
                throw NotFoundException.idNotExist("PoiEntity", poiId);
            form = new PoiForm();
            form.populateFrom(poiEntity);
        }

        model.addAttribute("form", form);

        return "admin/event-poi-edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createOrUpdate(@ModelAttribute("form") @Valid PoiForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {

            // FIXME: refaktoring

            // populateEventModel(model, new EventFilterParams(form.getEventKey()));
            return "admin/event-poi-edit";
        }

        PoiEntity poiEntity = new PoiEntity().populateFrom(form);

        try {
            if (poiEntity.isNew())
                poiEntity = poiService.createPoi(poiEntity);
            else
                poiEntity = poiService.updatePoi(poiEntity);

            return "redirect:/admin/event/poi/list?eventId=" + poiEntity.getEventId();

        } catch (MaPermissionDeniedException e) {
            addFormError(bindingResult, e);

            // FIXME: refaktoring
            // populateEventModel(model, new EventFilterParams(form.getEventKey()));
            return "admin/event-poi-edit";
        }
    }

    protected void populateEventModel(Model model) {
        // FIXME: refaktoring
/*        UserInEventFilter filter = new UserInEventFilter();
        filter.setEventIdVal(((EventFilterParams) params).getEventKey());
        ResultList<UserInEventEntity> result = userInEventService.findByFilter(filter, 0, null, UserInEventService.FLAG_FETCH_USER);
        List<UserEntity> users = new ArrayList<UserEntity>(result.getResultSize());
        for (UserInEventEntity inEvent : result) {
            users.add(inEvent.getUserEntity());
        }

        model.addAttribute("users", users);*/
    }
}
