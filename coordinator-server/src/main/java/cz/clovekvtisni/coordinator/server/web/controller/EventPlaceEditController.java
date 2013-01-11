package cz.clovekvtisni.coordinator.server.web.controller;

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
import cz.clovekvtisni.coordinator.server.web.model.FilterParams;
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
@RequestMapping("/admin/event/place/edit")
public class EventPlaceEditController extends AbstractEventController {

    @Autowired
    private PoiService poiService;

    @Autowired
    private UserInEventService userInEventService;

    @RequestMapping(method = RequestMethod.GET)
    public String edit(
            @ModelAttribute("params") EventFilterParams params,
            @RequestParam(value = "placeId", required = false) Long placeId,
            Model model) {

        EventEntity event = loadEventById(params.getEventId());
        model.addAttribute("event", event);

        PoiForm form;
        if (placeId == null) {
            UserEntity user = getLoggedUser();
            form = new PoiForm();
            form.setEventId(event.getId());
            form.setOrganizationId(user.getOrganizationId());
        } else {
            PoiEntity poiEntity = poiService.findById(placeId, 0l);
            params.setEventId(poiEntity.getEventId());
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
            EventEntity event = loadEventById(form.getEventId());
            model.addAttribute("event", event);
            populateEventModel(model, new EventFilterParams(form.getEventId()));
            return "admin/event-place-edit";
        }

        PoiEntity poiEntity = new PoiEntity().populateFrom(form);

        if (poiEntity.isNew())
            poiEntity = poiService.createPoi(poiEntity);
        else
            poiEntity = poiService.updatePoi(poiEntity);

        return "redirect:/admin/event/place/list?eventId=" + poiEntity.getEventId();
    }

    @Override
    protected void populateEventModel(Model model, FilterParams params) {
        super.populateEventModel(model, params);
        UserInEventFilter filter = new UserInEventFilter();
        filter.setEventIdVal(((EventFilterParams) params).getEventId());
        ResultList<UserInEventEntity> result = userInEventService.findByFilter(filter, 0, null, UserInEventService.FLAG_FETCH_USER);
        List<UserEntity> users = new ArrayList<UserEntity>(result.getResultSize());
        for (UserInEventEntity inEvent : result) {
            users.add(inEvent.getUserEntity());
        }

        model.addAttribute("users", users);
    }
}
