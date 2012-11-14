package cz.clovekvtisni.coordinator.server.web.model;

import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.domain.config.Role;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 13.11.12
 */
public class UserForm extends UserEntity {

    private String newPassword;

    private String confirmPassword;

    private Map<String, String> organizationMap;

    private Map<String, String> acceptableRoleMap;

    public void injectConfigValues(AppContext appContext, AuthorizationTool authorizationTool, CoordinatorConfig config) {
        List<Organization> organizations = config.getOrganizationList();
        List<Role> roles = config.getRoleList();
        UserEntity editor = appContext.getLoggedUser();

        organizationMap = new HashMap<String, String>(organizations.size());
        if (organizations != null) {
            for (Organization organization : organizations) {
                organizationMap.put(organization.getId(), organization.getName());
            }
        }

        acceptableRoleMap = new HashMap<String, String>();
        if (roles != null) {
            for (Role role : roles) {
                if (authorizationTool.canCreate(role.getId(), editor.getRoleIdList())) {
                    acceptableRoleMap.put(role.getId(), role.getName() + (role.getDescription() != null ? " - " + role.getDescription() : ""));
                }
            }
        }
    }

    public Map<String, String> getOrganizationMap() {
        return organizationMap;
    }

    public Map<String, String> getAcceptableRoleMap() {
        return acceptableRoleMap;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void postValidate(BindingResult bindingResult, MessageSource messageSource, Locale locale) {
        if (getNewPassword() != null && !getNewPassword().equals(getConfirmPassword())) {
            bindingResult.addError(new FieldError("form", "confirmPassword", null, false, null, null, messageSource.getMessage("error.PASSWORD_CONFIRM_FAILED", null, locale)));
        }
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
