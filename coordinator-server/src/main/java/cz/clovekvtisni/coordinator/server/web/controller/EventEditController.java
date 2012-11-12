package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.web.model.EventForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/admin/event/edit")
public class EventEditController extends AbstractController {

    @Autowired
    private EventService eventService;

    @CheckPermission("#helper.canCreate(eventEntity)")
    @RequestMapping(method = RequestMethod.GET)
    public String edit(@RequestParam(value = "eventId", required = false) String eventId, Model model) {
        EventForm form = new EventForm();

        if (eventId != null) {
            EventEntity event = eventService.findByEventId(eventId, 0);
            if (event == null)
                throw NotFoundException.idNotExist();
            form.populateFrom(event);
        }

        model.addAttribute("form", form);

        return "admin/event-edit";
    }

    @CheckPermission("#helper.canCreate(eventEntity)")
    @RequestMapping(method = RequestMethod.POST)
    public String createOrUpdate(@ModelAttribute("form") @Valid EventForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/event-edit";
        }

        EventEntity event = new EventEntity().populateFrom(form);

        try {
            if (event.isNew()) {
                eventService.createEvent(event);
            } else {
                eventService.updateEvent(event);
            }
        } catch (MaException e) {
            addFormError(bindingResult, e);
            return "admin/event-edit";
        }

        return "redirect:/admin/event/list";
    }
}
