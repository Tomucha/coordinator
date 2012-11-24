package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.service.OrganizationInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
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

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/find")
public class EventFindController extends AbstractController {

    @Autowired
    private EventService eventService;

    @Autowired
    private OrganizationInEventService organizationInEventService;

    @RequestMapping(value = "/organization-in-event/by-event-id",  method = RequestMethod.GET)
    public String detail(@RequestParam(value = "id", required = false) Long eventId, Model model) {
        OrganizationInEventFilter filter = new OrganizationInEventFilter();
        filter.setOrganizationIdVal(getLoggedUser().getOrganizationId());
        filter.setEventIdVal(eventId);

        ResultList<OrganizationInEventEntity> result = organizationInEventService.findByFilter(filter, 1, null, OrganizationInEventService.FLAG_FETCH_EVENT);
        if (result.getResultSize() == 0)
            throw NotFoundException.idNotExist();

        return "redirect:/admin/organization/register-to-event?id=" + result.firstResult().getId();
    }
}
