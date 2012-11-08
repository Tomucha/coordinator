package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.domain.AbstractModifiableEntity;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.PermissionCheckResultModel;
import cz.clovekvtisni.coordinator.server.security.SecurityTool;
import cz.clovekvtisni.coordinator.server.security.ServicePermissionCheckDescriptor;
import cz.clovekvtisni.coordinator.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:59 PM
 */
public class AbstractController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AppContext appContext;

    @Autowired
    protected SecurityTool securityTool;

    @ModelAttribute
    public void loggedUser(Model model) {
        PermissionCheckResultModel checkResultModel = securityTool.checkServicePermissions(new ServicePermissionCheckDescriptor[]{
                new ServicePermissionCheckDescriptor("logout", UserService.class, "logout", new Class[]{}, new Object[]{})
        });
        model.addAttribute("loggedUser", appContext.getLoggedUser());
        model.addAttribute("canDoLogout", checkResultModel.isPermitted("logout"));
    }

    protected void addFormError(BindingResult errors, MaException exception) {
        errors.addError(new ObjectError("globalErrors", exception.getMessage()));
    }

    protected void addFormError(BindingResult errors, String errorCode, Object... params) {
        errors.addError(new ObjectError("globalErrors", getMessage(errorCode, params)));
    }

    protected void addFieldError(BindingResult errors, String modelAttribute, String fieldName, Object fieldValue, String errCode, Object... errParams) {
        errors.addError(new FieldError(modelAttribute, fieldName, fieldValue, false, null, null, getMessage(errCode, errParams)));
    }


    protected String getMessage(String code, Object... params) {
        return messageSource.getMessage(code, params, getLocale());
    }

    protected Locale getLocale() {
        return appContext.getLocale();
    }

    protected UserEntity getLoggedUser() {
        return appContext.getLoggedUser();
    }
}
