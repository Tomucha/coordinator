package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.EventFilter;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.service.OrganizationInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.model.OrganizationInEventForm;
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
 * Date: 20.11.12
 */
@Controller
@RequestMapping("/admin/organization/register-to-event")
public class OrganizationInEventController extends AbstractController {

    @Autowired
    private EventService eventService;

    @Autowired
    private OrganizationInEventService organizationInEventService;

    @RequestMapping(method = RequestMethod.GET)
    public String getForm(@RequestParam(value = "id", required = false) Long organizationInEventId, Model model) {
        if (organizationInEventId != null) {
            OrganizationInEventEntity registration = organizationInEventService.findById(organizationInEventId, 0l);
            if (registration == null)
                throw NotFoundException.idNotExist(OrganizationInEventEntity.class.getSimpleName(), organizationInEventId);
            model.addAttribute("form", new OrganizationInEventForm().populateFrom(registration));
        } else {
            OrganizationInEventForm form = new OrganizationInEventForm();
            form.setOrganizationId(getLoggedUser().getOrganizationId());
            model.addAttribute("form", form);
        }

        populateModel(model);

        return "admin/organization-register-in-event";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createOrUpdate(@ModelAttribute("form") @Valid OrganizationInEventForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            populateModel(model);
            return "admin/organization-register-in-event";
        }

        try {
            OrganizationInEventEntity entity = new OrganizationInEventEntity().populateFrom(form);
            if (entity.isNew())
                organizationInEventService.create(entity);
            else
                organizationInEventService.update(entity);

        } catch (MaException e) {
            addFormError(bindingResult, e);
            populateModel(model);
            return "admin/organization-register-in-event";
        }

        return "redirect:/admin/event/list";
    }

    public void populateModel(Model model) {
        UserEntity loggedUser = getLoggedUser();
        if (loggedUser.getOrganizationId() == null) return;
        OrganizationInEventFilter filter = new OrganizationInEventFilter();
        filter.setOrganizationIdVal(loggedUser.getOrganizationId());
        List<EventEntity> inEvents = eventService.findByOrganizationFilter(filter, 0, null, 0l).getResult();
        ResultList<EventEntity> allEvents = eventService.findByFilter(new EventFilter(), 0, null, 0l);
        List<EventEntity> result = new ArrayList<EventEntity>();
        for (EventEntity event : allEvents.getResult()) {
            if (!inEvents.contains(event))
                result.add(event);
        }

        model.addAttribute("eventList", result);
    }
}
