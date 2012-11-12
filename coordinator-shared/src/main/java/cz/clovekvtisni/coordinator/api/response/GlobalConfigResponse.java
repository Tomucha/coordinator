package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.domain.config.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 7.11.12
 */
public class GlobalConfigResponse implements ApiResponseData {

    private Role[] roleList;

    private Skill[] skillList;

    private Equipment[] equipmentList;

    private Organization[] organizationList;

    private Workflow[] workflowList;

    private PoiCategory[] poiCategoryList;

    public Role[] getRoleList() {
        return roleList;
    }

    public void setRoleList(Role[] roleList) {
        this.roleList = roleList;
    }

    public Skill[] getSkillList() {
        return skillList;
    }

    public void setSkillList(Skill[] skillList) {
        this.skillList = skillList;
    }

    public Equipment[] getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(Equipment[] equipmentList) {
        this.equipmentList = equipmentList;
    }

    public Organization[] getOrganizationList() {
        return organizationList;
    }

    public void setOrganizationList(Organization[] organizationList) {
        this.organizationList = organizationList;
    }

    public Workflow[] getWorkflowList() {
        return workflowList;
    }

    public void setWorkflowList(Workflow[] workflowList) {
        this.workflowList = workflowList;
    }

    public PoiCategory[] getPoiCategoryList() {
        return poiCategoryList;
    }

    public void setPoiCategoryList(PoiCategory[] poiCategoryList) {
        this.poiCategoryList = poiCategoryList;
    }
}
