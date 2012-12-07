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
import cz.clovekvtisni.coordinator.server.web.model.FilterParams;
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
@RequestMapping("/admin/event/detail")
public class EventDetailController extends AbstractEventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private OrganizationInEventService organizationInEventService;

    @RequestMapping(method = RequestMethod.GET)
    public String getForm(@ModelAttribute("params") EventFilterParams params, Model model) {

        UserEntity user = getLoggedUser();

        if (params.getEventId() != null) {
            OrganizationInEventFilter inEventFilter = new OrganizationInEventFilter();
            inEventFilter.setOrganizationIdVal(user.getOrganizationId());
            inEventFilter.setEventIdVal(params.getEventId());
            ResultList<OrganizationInEventEntity> result = organizationInEventService.findByFilter(inEventFilter, 0, null, OrganizationInEventService.FLAG_FETCH_EVENT);
            if (result.getResultSize() == 0 || result.firstResult() == null)
                throw NotFoundException.idNotExist();
            OrganizationInEventEntity registration = result.firstResult();
            model.addAttribute("form", new OrganizationInEventForm().populateFrom(registration));
            populateEventModel(model, new EventFilterParams(registration.getEventEntity()));

        } else {
            OrganizationInEventForm form = new OrganizationInEventForm();
            form.setOrganizationId(user.getOrganizationId());
            model.addAttribute("form", form);
            populateEventModel(model, null);
        }

        populateModel(model);

        return "admin/event-detail";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createOrUpdate(@ModelAttribute("form") @Valid OrganizationInEventForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            populateEventModel(model, new EventFilterParams(loadEventById(form.getEventId())));
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
            populateEventModel(model, new EventFilterParams(loadEventById(form.getEventId())));
            addFormError(bindingResult, e);
            populateModel(model);
            return "admin/event-detail";
        }

        return "redirect:/admin/event/list";
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

    public static Breadcrumb getBreadcrumb(FilterParams params) {
        return new Breadcrumb(params, "/admin/event/detail", "breadcrumb.eventDetail", AuthorizationTool.ADMIN);
    }
}
