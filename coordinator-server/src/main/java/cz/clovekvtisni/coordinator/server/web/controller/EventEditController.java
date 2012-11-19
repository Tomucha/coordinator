package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.web.model.EventForm;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
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
@RequestMapping("/admin/event/edit")
public class EventEditController extends AbstractController {

    @Autowired
    private EventService eventService;

    @CheckPermission("#helper.canCreate(eventEntity)") // nefunkcni, TODO
    @RequestMapping(method = RequestMethod.GET)
    public String edit(@RequestParam(value = "id", required = false) Long eventId, Model model) {
        EventForm form = new EventForm();

        if (eventId != null) {
            EventEntity event = eventService.findById(eventId, EventService.FLAG_FETCH_LOCATIONS);
            if (event == null)
                throw NotFoundException.idNotExist();
            form.populateFrom(event);
        }

        model.addAttribute("form", form);
        model.addAttribute("breadcrumbs", breadcrumbs(form));

        return "admin/event-edit";
    }

    @CheckPermission("#helper.canCreate(eventEntity)")
    @RequestMapping(method = RequestMethod.POST)
    public String createOrUpdate(@ModelAttribute("form") @Valid EventForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("breadcrumbs", breadcrumbs(form));
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
            model.addAttribute("breadcrumbs", breadcrumbs(form));
            addFormError(bindingResult, e);
            return "admin/event-edit";
        }

        return "redirect:/admin/event/list";
    }

    public static Breadcrumb getBreadcrumb(EventEntity entity) {
        return new Breadcrumb(entity, "/admin/event/edit", "breadcrumb.eventEdit");
    }

    public Breadcrumb[] breadcrumbs(EventEntity entity) {
        if (entity == null || entity.isNew()) {
            return new Breadcrumb[] {
                    UserListController.getBreadcrumb(),
                    EventListController.getBreadcrumb()
            };

        } else {
            return new Breadcrumb[] {
                    HomeController.getBreadcrumb(),
                    EventMapController.getBreadcrumb(entity),
                    EventUsersController.getBreadcrumb(entity),
                    EventPlacesController.getBreadcrumb(entity),
                    EventEditController.getBreadcrumb(entity)
            };
        }
    }
}
