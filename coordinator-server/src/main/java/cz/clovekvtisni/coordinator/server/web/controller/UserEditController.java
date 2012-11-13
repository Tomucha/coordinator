package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.web.model.EventForm;
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
@RequestMapping("/admin/user/edit")
public class UserEditController extends AbstractController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String edit(@RequestParam(value = "userId", required = false) Long userId, Model model) {
        /*
        EventForm form = new EventForm();

        if (userId != null) {
            UserEntity user = userService.findById(userId, 0);
            if (user == null)
                throw NotFoundException.idNotExist();
            form.populateFrom(user);
        }

        model.addAttribute("form", form);
          */
        return "admin/event-edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createOrUpdate(@ModelAttribute("form") @Valid EventForm form, BindingResult bindingResult) {
        /*
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
          */
        return "redirect:/admin/event/list";
    }
}
