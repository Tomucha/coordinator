package cz.clovekvtisni.coordinator.server.security;

import cz.clovekvtisni.coordinator.domain.config.Role;
import cz.clovekvtisni.coordinator.domain.config.RolePermission;
import cz.clovekvtisni.coordinator.domain.config.Workflow;
import cz.clovekvtisni.coordinator.domain.config.WorkflowState;
import cz.clovekvtisni.coordinator.server.domain.*;
import cz.clovekvtisni.coordinator.util.ValueTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 12.11.12
 */
@Component
public class AuthorizationTool {

    public static final String SUPERADMIN = "SUPERADMIN";

    public static final String ADMIN = "ADMIN";

    public static final String BACKEND = "BACKEND";

    public static final String COORDINATOR = "COORDINATOR";

    public static final String ANONYMOUS = "ANONYMOUS";

    private Map<String, Role> roleMap;

    private Map<String, List<String>> roleParentMap;

    private CoordinatorConfig config = null;

    @Autowired
    public void setConfig(CoordinatorConfig config) {
        this.config = config;
        roleMap = config.getRoleMap();


        roleParentMap = new HashMap<String, List<String>>(roleMap.size());
        for (Role role : roleMap.values()) {
            List<String> ids = new ArrayList<String>();
            roleParentMap.put(role.getId(), ids);
            String parentId = role.getExtendsRoleId();
            while (parentId != null) {
                ids.add(parentId);
                Role parent = roleMap.get(parentId);
                parentId = parent.getExtendsRoleId();
            }
        }
    }

    public boolean hasAnyPermission(UserEntity user, RolePermission... permissions) {
        String[] roleIdList = user.getRoleIdList();
        if (roleIdList == null)
            return false;
        for (String roleId : roleIdList) {
            Role role = roleMap.get(roleId);
            if (role.hasAnyPermission(permissions))
                return true;
        }
        return false;
    }

    public boolean hasAnyPermission(UserInEventEntity inEventEntity, RolePermission... permissions) {
        if (hasAnyPermission(inEventEntity.getUserEntity(), permissions))
            return true;
        for (UserGroupEntity userGroup : inEventEntity.getGroupEntities()) {
            if (userGroup.getRoleId() == null)
                continue;
            Role role = roleMap.get(userGroup.getRoleId());
            if (role.hasAnyPermission(permissions))
                return true;
        }
        return false;
    }

    public boolean canCreate(String roleId, String[] creatorRoles) {
        return canCreate(roleId, creatorRoles != null ? Arrays.asList(creatorRoles) : new ArrayList<String>(0));
    }

    public boolean canCreate(String roleId, List<String> creatorRoles) {
        if (roleId == null || creatorRoles == null) return false;
        if (roleId.equals(SUPERADMIN) || roleId.equals(ADMIN))
            return isAuthorized(Arrays.asList(new String[] {SUPERADMIN}), creatorRoles);
        if (roleId.equals(COORDINATOR))
            return isAuthorized(Arrays.asList(new String[] {ADMIN}), creatorRoles);

        return true;
    }

    @Deprecated
    public boolean hasRole(String roleId, UserEntity user) {
        if (user == null || user.getRoleIdList() == null) return roleId == null;
        return isAuthorized(Arrays.asList(new String[] {roleId}), Arrays.asList(user.getRoleIdList()));
    }

    @Deprecated
    public boolean isAuthorized(String[] roles, UserEntity user) {
        return isAuthorized(Arrays.asList(roles), user != null && user.getRoleIdList() != null ? Arrays.asList(user.getRoleIdList()) : null);
    }

    @Deprecated
    public boolean isAuthorized(List<String> needOneOfRoles, List<String>... hasAllRoles) {
        if (needOneOfRoles == null) return true;
        if (hasAllRoles == null) return needOneOfRoles.size() == 0;

        Set<String> hasRoles = new HashSet<String>();
        for (List<String> roles : hasAllRoles) {
            if (roles != null) {
                for (String role : roles) {
                    hasRoles.add(role);
                    hasRoles.addAll(roleParentMap.get(role));
                }
            }
        }

        if (hasRoles.contains(SUPERADMIN)) {
            return true;
        }

        for (String needRole : needOneOfRoles) {
            if (hasRoles.contains(needRole))
                return true;
        }

        return false;
    }

    public boolean isCanBeStartedBy(PoiEntity poi, UserEntity user) {
        Workflow workflow = poi.getWorkflow();
        if (workflow == null)
            return true;
        return  isAuthorized(workflow.getCanBeStartedBy(), user);
    }

    public Set<String> findRolesWithReadPermission(PoiEntity poi) {
        Set<String> roles = new HashSet<String>();

        if (poi.isImportant())
            roles.add(ANONYMOUS);

        if (!ValueTool.isEmpty(poi.getWorkflowStateId())) {
            WorkflowState state = config.getWorkflowMap().get(poi.getWorkflowId()).getStateMap().get(poi.getWorkflowStateId());
            String[] visibleForRole = state.getVisibleForRole();
            if (visibleForRole != null)
                roles.addAll(Arrays.asList(visibleForRole));
            String[] editableForRole = state.getEditableForRole();
            if (editableForRole != null)
                roles.addAll(Arrays.asList(editableForRole));
        }

        // children included
        Set<String> children = new HashSet<String>();
        for (String parentRoleId : roles) {
            for (Map.Entry<String, List<String>> entry : roleParentMap.entrySet()) {
                if (!parentRoleId.equals(entry.getKey()) && entry.getValue().contains(parentRoleId)) {
                    children.add(parentRoleId);
                }
            }
        }
        roles.addAll(children);

        return roles;
    }
}
