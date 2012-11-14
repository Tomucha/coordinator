package cz.clovekvtisni.coordinator.server.web.model;

import cz.clovekvtisni.coordinator.domain.UserEquipment;
import cz.clovekvtisni.coordinator.domain.config.Equipment;
import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.domain.config.Role;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEquipmentEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

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

    private List<Equipment> allEquipmentList;

    private Set<String> selectedEquipment;

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

        allEquipmentList = config.getEquipmentList();
    }

    public List<Equipment> getAllEquipmentList() {
        return allEquipmentList;
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

    public Set<String> getSelectedEquipment() {
        return selectedEquipment;
    }

    public void setSelectedEquipment(Set<String> selectedEquipment) {
        this.selectedEquipment = selectedEquipment;
    }

    public UserEntity export(UserEntity user) {
        UserEntity exported = new UserEntity().populateFrom(this);

        if (selectedEquipment == null) {
            selectedEquipment = new HashSet<String>();
        }

        List<UserEquipmentEntity> equipmentList = new ArrayList<UserEquipmentEntity>();
        if (!isNew()) {
            for (UserEquipmentEntity equipmentEntity : user.getEquipmentList()) {
                if (selectedEquipment.contains(equipmentEntity.getEquipmentId())) {
                    selectedEquipment.remove(equipmentEntity.getEquipmentId());

                } else {
                    equipmentEntity.setDeletedDate(new Date());
                }
                equipmentList.add(equipmentEntity);
            }
        }
        for (String equipmentId : selectedEquipment) {
            UserEquipmentEntity equipment = new UserEquipmentEntity();
            equipment.setEquipmentId(equipmentId);
            equipmentList.add(equipment);
        }
        exported.setEquipmentList(equipmentList.toArray(new UserEquipmentEntity[0]));

        return exported;
    }

    @Override
    public UserEntity populateFrom(UserEntity entity) {
        super.populateFrom(entity);
        UserEquipmentEntity[] equipmentList = entity.getEquipmentList();
        if (equipmentList != null) {
            selectedEquipment = new HashSet<String>(equipmentList.length);
            for (UserEquipmentEntity equipmentEntity : equipmentList) {
                selectedEquipment.add(equipmentEntity.getEquipmentId());
            }
        }

        return this;
    }
}
