package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.tool.objectify.UniqueKeyViolation;
import cz.clovekvtisni.coordinator.server.web.model.EventForm;
import cz.clovekvtisni.coordinator.server.web.model.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

    @Autowired
    private CoordinatorConfig config;

    @Autowired
    private AuthorizationTool authorizationTool;

    @RequestMapping(method = RequestMethod.GET)
    public String edit(@RequestParam(value = "userId", required = false) Long userId, Model model) {
        UserForm form = new UserForm();
        form.injectConfigValues(appContext, authorizationTool, config);

        if (userId != null) {
            UserEntity user = userService.findById(userId, UserService.FLAG_FETCH_EQUIPMENT);
            if (user == null)
                throw NotFoundException.idNotExist();
            form.populateFrom(user);
        }

        model.addAttribute("form", form);

        return "admin/user-edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createOrUpdate(@ModelAttribute("form") @Valid UserForm form, BindingResult bindingResult) {
        form.injectConfigValues(appContext, authorizationTool, config);
        form.postValidate(bindingResult, messageSource, appContext.getLocale());

        if (bindingResult.hasErrors()) {
            return "admin/user-edit";
        }

        try {
            if (form.isNew()) {
                UserEntity user = form.export(new UserEntity());
                user.setPassword(form.getNewPassword());
                userService.createUser(user);

            } else {
                UserEntity user = userService.findById(form.getId(), UserService.FLAG_FETCH_EQUIPMENT);
                UserEntity toSave = form.export(user);
                userService.updateUser(toSave);
            }
        } catch (UniqueKeyViolation e) {
            addFieldError(bindingResult, "form", e.getProperty().toString().toLowerCase(), null, "error.UNIQUE_KEY_VIOLATION");
            return "admin/user-edit";

        } catch (MaException e) {
            addFormError(bindingResult, e);
            return "admin/user-edit";
        }

        return "redirect:/admin/user/list";
    }
}
