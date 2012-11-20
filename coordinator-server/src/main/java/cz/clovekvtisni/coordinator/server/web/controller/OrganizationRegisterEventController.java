package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.service.OrganizationInEventService;
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

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 20.11.12
 */
@Controller
@RequestMapping("/admin/organization/register-to-event")
public class OrganizationRegisterEventController extends AbstractController {

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
            model.addAttribute("form", new OrganizationInEventForm());
        }

        return "admin/organization-register-in-event";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createOrUpdate(@ModelAttribute("form") @Valid OrganizationInEventForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/organization-register-in-event";
        }

        try {
            OrganizationInEventEntity user = new OrganizationInEventEntity().populateFrom(form);
            if (form.isNew())
                organizationInEventService.create(form);
            else
                organizationInEventService.update(form);

        } catch (MaException e) {
            addFormError(bindingResult, e);
            return "admin/organization-register-in-event";
        }

        return "redirect:/admin/event/list";
    }

    private void populateModel(Model model) {

    }
}
