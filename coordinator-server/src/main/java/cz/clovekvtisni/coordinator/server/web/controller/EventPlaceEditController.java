package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.model.EventUserForm;
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

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.12.12
 */
@Controller
@RequestMapping("/admin/event/place/edit")
public class EventPlaceEditController extends AbstractEventController {

    @Autowired
    private PoiService poiService;

    @RequestMapping(method = RequestMethod.GET)
    public String edit(
            @ModelAttribute("params") EventFilterParams params,
            @RequestParam(value = "placeId", required = false) Long placeId,
            Model model) {

        EventEntity event = getEventById(params.getEventId());

        PoiForm form;
        if (placeId == null) {
            UserEntity user = getLoggedUser();
            form = new PoiForm();
            form.setEventId(event.getId());
            form.setOrganizationId(user.getOrganizationId());
        } else {
            PoiEntity poiEntity = poiService.findById(placeId, 0l);
            if (poiEntity == null)
                throw NotFoundException.idNotExist("PoiEntity", placeId);
            form = new PoiForm();
            form.populateFrom(poiEntity);
        }

        model.addAttribute("form", form);
        populateEventModel(model, params);

        return "admin/event-place-edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createOrUpdate(@ModelAttribute("form") @Valid PoiForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            populateEventModel(model, new EventFilterParams(form.getEventId()));
            return "admin/event-user-edit";
        }

        PoiEntity poiEntity = new PoiEntity().populateFrom(form);

        if (poiEntity.isNew())
            poiEntity = poiService.createPoi(poiEntity);
        else
            poiEntity = poiService.updatePoi(poiEntity);

        return "redirect:/admin/event/places?eventId=" + poiEntity.getEventId();
    }
}
