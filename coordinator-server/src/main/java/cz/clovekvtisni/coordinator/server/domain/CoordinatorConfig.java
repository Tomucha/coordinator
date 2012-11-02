package cz.clovekvtisni.coordinator.server.domain;

import cz.clovekvtisni.coordinator.domain.*;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
@Root(name = "coordinator")
public class CoordinatorConfig {

    @ElementList(type = Role.class, name = "role_list", required = false)
    private List<Role> roleList;

    @ElementList(type = Skill.class, name = "skill_list", required = false)
    private List<Skill> skillList;

    @ElementList(type = Equipment.class, name = "equipment_list", required = false)
    private List<Equipment> equipmentList;

    @ElementList(type = Organization.class, name = "organization_list", required = false)
    private List<Organization> organizationList;

    @ElementList(type = Workflow.class, name = "workflow_list", required = false)
    private List<Workflow> workflowList;

    @ElementList(type = PoiCategory.class, name = "poi_category_list", required = false)
    private List<PoiCategory> poiCategoryList;

    public List<Role> getRoleList() {
        return roleList;
    }

    public Map<String, Role> getRoleMap() {
        if (roleList == null) return new HashMap<String, Role>();
        Map<String, Role> map = new HashMap<String, Role>(roleList.size());
        for (Role role : roleList) {
            map.put(role.getId(), role);
        }

        return map;
    }

    public Map<String, Workflow> getWorkflowMap() {
        if (workflowList == null) return new HashMap<String, Workflow>();
        Map<String, Workflow> map = new HashMap<String, Workflow>(workflowList.size());
        for (Workflow workflow : workflowList) {
            map.put(workflow.getId(), workflow);
        }

        return map;
    }

    public List<Skill> getSkillList() {
        return skillList;
    }

    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    public List<Organization> getOrganizationList() {
        return organizationList;
    }

    public List<Workflow> getWorkflowList() {
        return workflowList;
    }

    public List<PoiCategory> getPoiCategoryList() {
        return poiCategoryList;
    }

    @Validate
    public void validate() {
        Map<String, Role> roleMap = getRoleMap();
        Map<String, Workflow> workflowMap = getWorkflowMap();

        if (roleList != null) {
            for (Role role : roleList) {
                checkEntityExist(roleMap, role.getExtendsRoleId());
            }
        }
        if (workflowList != null) {
            for (Workflow workflow : workflowList) {
                checkEntityExist(roleMap, workflow.getCanBeStartedBy());
                if (workflow.getStates() != null) {
                    for (WorkflowState state : workflow.getStates()) {
                        checkEntityExist(roleMap, state, state.getEditableForRole());
                        checkEntityExist(roleMap, state, state.getVisibleForRole());
                    }
                }
            }
        }
        
        if (poiCategoryList != null) {
            for (PoiCategory poiCategory : poiCategoryList) {
                checkEntityExist(workflowMap, poiCategory, poiCategory.getWorkflowId());
            }
        }
    }

    private <T extends AbstractStaticEntity> void checkEntityExist(Map<String, T> roleMap, Object source, String... roles) {
        if (roles == null) return;
        for (String roleId : roles) {
            if (roleId != null && !roleMap.containsKey(roleId)) {
                throw new IllegalStateException("Inconsistent configuration. Reference to nonexistent entity in " + source);
            }
        }
    }

    @Override
    public String toString() {
        return "CoordinatorConfig{" +
                "roleList=" + roleList +
                ", skillList=" + skillList +
                ", equipmentList=" + equipmentList +
                ", organizationList=" + organizationList +
                ", workflowList=" + workflowList +
                ", poiCategoryList=" + poiCategoryList +
                '}';
    }
}
