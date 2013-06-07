package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.EventFilter;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.service.OrganizationInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.model.OrganizationInEventForm;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 20.11.12
 */
@Controller
@RequestMapping("/admin/event-register")
public class EventRegisterController extends AbstractEventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private OrganizationInEventService organizationInEventService;

    @RequestMapping(method = RequestMethod.GET)
    public String getForm(
        @ModelAttribute("params") EventFilterParams params,
        Model model)
    {
        if (params.getEventId() == null)
            throw NotFoundException.idNotExist("eventEntity", params.getEventId()); // TODO do filtru s tim, nebo nekam
        UserEntity user = getLoggedUser();

        OrganizationInEventEntity registration = organizationInEventService.findEventInOrganization(params.getEventId(), user.getOrganizationId(), OrganizationInEventService.FLAG_FETCH_EVENT);
        if (registration != null)
            return "redirect: " + params.getRetUrl();


        // organization doesn't have a OrganizationInEventEntity yet
        registration = new OrganizationInEventEntity();
        registration.setEventEntity(appContext.getActiveEvent());
        registration.setEventId(params.getEventId());
        registration.setOrganizationId(user.getOrganizationId());
        OrganizationInEventForm form = new OrganizationInEventForm();
        form.populateFrom(registration);
        model.addAttribute("form", form);
        if (params.getRetUrl() == null)
            form.setRetUrl("/admin/event/map?eventId=" + params.getEventId());

        populateModel(model);

        return "admin/event-register";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createOrUpdate(@ModelAttribute("form") @Valid OrganizationInEventForm form, BindingResult bindingResult, Model model) {
        if (form.getRetUrl() == null)
            form.setRetUrl("/admin/event/map?eventId=" + form.getEventId());

        if (bindingResult.hasErrors()) {

            //
            // populateEventModel(model, new EventFilterParams(loadEventById(form.getEventKey())));
            populateModel(model);
            return "admin/event-detail";
        }

        try {
            OrganizationInEventEntity entity = new OrganizationInEventEntity().populateFrom(form);
            if (entity.isNew())
                organizationInEventService.create(entity);
            else
                organizationInEventService.update(entity);

        } catch (MaException e) {
            // FIXME: refaktoring
            //populateEventModel(model, new EventFilterParams(loadEventById(form.getEventKey())));
            addFormError(bindingResult, e);
            populateModel(model);
            return "admin/event-detail";
        }

        return "redirect: " + form.getRetUrl();
    }

    protected void populateModel(Model model) {
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
