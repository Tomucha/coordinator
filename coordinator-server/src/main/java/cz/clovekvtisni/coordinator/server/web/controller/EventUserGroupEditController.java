package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserGroupEntity;
import cz.clovekvtisni.coordinator.server.service.UserGroupService;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.model.PoiForm;
import cz.clovekvtisni.coordinator.server.web.model.UserGroupForm;
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
@RequestMapping("/admin/event/user-group/edit")
public class EventUserGroupEditController extends AbstractEventController {

    @Autowired
    private UserGroupService userGroupService;

    @RequestMapping(method = RequestMethod.GET)
    public String edit(
            @ModelAttribute("params") EventFilterParams params,
            @RequestParam(value = "groupId", required = false) Long groupId,
            Model model) {

        UserGroupForm form;
        if (groupId == null) {
            UserEntity user = getLoggedUser();
            form = new UserGroupForm();
            form.setEventId(appContext.getActiveEvent().getId());
            form.setOrganizationId(user.getOrganizationId());
        } else {
            UserGroupEntity userGroupEntity = userGroupService.findById(groupId, 0l);
            params.setEventId(userGroupEntity.getEventId());
            if (userGroupEntity == null)
                throw NotFoundException.idNotExist("UserGroupEntity", groupId);
            form = new UserGroupForm();
            form.populateFrom(userGroupEntity);
        }

        model.addAttribute("form", form);

        return "admin/event-usergroup-edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createOrUpdate(@ModelAttribute("form") @Valid UserGroupForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // FIXME: refaktoring

            // populateEventModel(model, new EventFilterParams(form.getEventId()));
            return "admin/event-group-edit";
        }

        if ("".equals(form.getRoleId()))
            form.setRoleId(null);
        UserGroupEntity userGroupEntity = new UserGroupEntity().populateFrom(form);

        if (userGroupEntity.isNew())
            userGroupEntity = userGroupService.createUserGroup(userGroupEntity);
        else
            userGroupEntity = userGroupService.updateUserGroup(userGroupEntity);

        return "redirect:/admin/event/user/list?eventId=" + userGroupEntity.getEventId();
    }
}
