package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.model.EventForm;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping("/superadmin/event/edit")
public class EventEditController extends AbstractSuperadminController {

    @Autowired
    private EventService eventService;

    @RequestMapping(method = RequestMethod.GET)
    public String edit(@ModelAttribute("params") EventFilterParams params, Model model) {
        EventForm form = new EventForm();
        if (appContext.getActiveEvent() != null) {
            form.populateFrom(appContext.getActiveEvent());
        }
        model.addAttribute("form", form);
        return "superadmin/event-edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createOrUpdate(@ModelAttribute("form") @Valid EventForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            //populateEventModel(model, new EventFilterParams(form));
            return "superadmin/event-edit";
        }

        EventEntity event = new EventEntity().populateFrom(form);

        try {
            if (event.isNew()) {
                eventService.createEvent(event);
            } else {
                eventService.updateEvent(event);
            }
        } catch (MaException e) {
            //populateEventModel(model, new EventFilterParams(form));
            addFormError(bindingResult, e);
            return "superadmin/event-edit";
        }

        return "redirect:/superadmin/event/list";
    }

    public static Breadcrumb getBreadcrumb(EventEntity params) {
        return new Breadcrumb(params, "/superadmin/event/edit", "breadcrumb.eventEdit", AuthorizationTool.SUPERADMIN);
    }
}
