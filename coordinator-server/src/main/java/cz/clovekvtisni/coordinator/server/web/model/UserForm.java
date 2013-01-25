package cz.clovekvtisni.coordinator.server.web.model;

import cz.clovekvtisni.coordinator.domain.config.Equipment;
import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.domain.config.Role;
import cz.clovekvtisni.coordinator.domain.config.Skill;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEquipmentEntity;
import cz.clovekvtisni.coordinator.server.domain.UserSkillEntity;
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

    private String confirmPassword;

    private String organizationId;

    private Map<String, String> acceptableRoleMap;

    private List<Equipment> allEquipmentList;

    private List<Skill> allSkillList;

    public void injectConfigValues(AppContext appContext, AuthorizationTool authorizationTool, CoordinatorConfig config) {
        List<Organization> organizations = config.getOrganizationList();
        List<Role> roles = config.getRoleList();
        UserEntity editor = appContext.getLoggedUser();

        acceptableRoleMap = new HashMap<String, String>();
        if (roles != null) {
            for (Role role : roles) {
                if (authorizationTool.canCreate(role.getId(), editor.getRoleIdList())) {
                    acceptableRoleMap.put(role.getId(), role.getName() + (role.getDescription() != null ? " - " + role.getDescription() : ""));
                }
            }
        }

        allEquipmentList = config.getEquipmentList();

        allSkillList = config.getSkillList();
    }

    public List<Skill> getAllSkillList() {
        return allSkillList;
    }

    public List<Equipment> getAllEquipmentList() {
        return allEquipmentList;
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
        if (getPassword() != null && !getPassword().equals(getConfirmPassword())) {
            bindingResult.addError(new FieldError("form", "confirmPassword", getPassword(), false, null, null, messageSource.getMessage("error.PASSWORD_CONFIRM_FAILED", null, locale)));
        }
    }

    public Set<String> getSelectedEquipment() {
        UserEquipmentEntity[] equipmentList = getEquipmentEntityList();
        if (equipmentList == null) return new HashSet<String>();

        Set<String> selectedEquipment = new HashSet<String>(equipmentList.length);
        for (UserEquipmentEntity equipmentEntity : equipmentList) {
            selectedEquipment.add(equipmentEntity.getEquipmentId());
        }
        
        return selectedEquipment;
    }

    public void setSelectedEquipment(Set<String> selectedEquipment) {
        if (selectedEquipment == null) {
            setEquipmentEntityList(new UserEquipmentEntity[0]);
            return;
        }
        List<UserEquipmentEntity> equipmentEntityList = new ArrayList<UserEquipmentEntity>(selectedEquipment.size());
        for (String equipmentId : selectedEquipment) {
            UserEquipmentEntity equipmentEntity = new UserEquipmentEntity();
            equipmentEntity.setEquipmentId(equipmentId);
            equipmentEntityList.add(equipmentEntity);
        }
        setEquipmentEntityList(equipmentEntityList.toArray(new UserEquipmentEntity[0]));
    }

    public Set<String> getSelectedSkill() {
        UserSkillEntity[] skillList = getSkillEntityList();
        if (skillList == null) return new HashSet<String>();

        Set<String> selectedSkill = new HashSet<String>(skillList.length);
        for (UserSkillEntity skillEntity : skillList) {
            selectedSkill.add(skillEntity.getSkillId());
        }

        return selectedSkill;
    }

    public void setSelectedSkill(Set<String> selectedSkill) {
        if (selectedSkill == null) {
            setSkillEntityList(new UserSkillEntity[0]);
            return;
        }
        List<UserSkillEntity> skillEntityList = new ArrayList<UserSkillEntity>(selectedSkill.size());
        for (String skillId : selectedSkill) {
            UserSkillEntity skillEntity = new UserSkillEntity();
            skillEntity.setSkillId(skillId);
            skillEntityList.add(skillEntity);
        }
        setSkillEntityList(skillEntityList.toArray(new UserSkillEntity[0]));
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

}
