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
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
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
@RequestMapping("/superadmin/user/edit")
public class UserEditController extends AbstractSuperadminController {

    @RequestMapping(method = RequestMethod.GET)
    public String edit(@RequestParam(value = "id", required = false) Long userId, Model model) {
        UserForm form = new UserForm();
        form.injectConfigValues(appContext, authorizationTool, config);

        if (userId != null) {
            UserEntity user = userService.findById(userId, UserService.FLAG_FETCH_EQUIPMENT | UserService.FLAG_FETCH_SKILLS);
            if (user == null)
                throw NotFoundException.idNotExist();
            form.populateFrom(user);
        }

        model.addAttribute("form", form);

        return "superadmin/user-edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createOrUpdate(@ModelAttribute("form") @Valid UserForm form, BindingResult bindingResult) {
        form.injectConfigValues(appContext, authorizationTool, config);
        form.postValidate(bindingResult, messageSource, appContext.getLocale());

        if (bindingResult.hasErrors()) {
            return "superadmin/user-edit";
        }

        try {
            UserEntity user = new UserEntity().populateFrom(form);
            if (form.isNew())
                userService.createUser(user);
            else
                userService.updateUser(user);

        } catch (UniqueKeyViolation e) {
            addFieldError(bindingResult, "form", e.getProperty().toString().toLowerCase(), form.getEmail(), "error.UNIQUE_KEY_VIOLATION");
            return "superadmin/user-edit";

        } catch (MaException e) {
            addFormError(bindingResult, e);
            return "superadmin/user-edit";
        }

        return "redirect:/superadmin/user/list";
    }

    @ModelAttribute("breadcrumbs")
    public Breadcrumb[] breadcrumbs() {
        return new Breadcrumb[] {
                UserListController.getBreadcrumb(),
                EventListController.getBreadcrumb()
        };
    }

}
