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

    public static final String ANONYMOUS = "ANONYMOUS";

    private Map<String, Role> roleMap;

    private Map<String, List<String>> roleParentMap;

    @Autowired
    public void setConfig(CoordinatorConfig config) {
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

    @Deprecated
    public boolean canCreate(String roleId, String[] creatorRoles) {
        return canCreate(roleId, Arrays.asList(creatorRoles));
    }

    @Deprecated
    public boolean canCreate(String roleId, List<String> creatorRoles) {
        if (roleId == null || creatorRoles == null) return false;
        if (roleId.equals(SUPERADMIN) || roleId.equals(ADMIN))
            return isAuthorized(Arrays.asList(new String[] {SUPERADMIN}), creatorRoles);
        if (roleId.equals(BACKEND))
            return isAuthorized(Arrays.asList(new String[] {ADMIN}), creatorRoles);

        return true;
    }

    // TODO prejmenovat na isAuthorized()
    @Deprecated
    public boolean hasRole(String roleId, UserEntity user) {
        if (user == null || user.getRoleIdList() == null) return roleId == null;
        return isAuthorized(Arrays.asList(new String[] {roleId}), Arrays.asList(user.getRoleIdList()));
    }

    public boolean isAuthorized(String[] roles, UserEntity user) {
        return isAuthorized(Arrays.asList(roles), user != null && user.getRoleIdList() != null ? Arrays.asList(user.getRoleIdList()) : null);
    }

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

    // TODO zohlednit userGroup
    public boolean isVisibleFor(PoiEntity poi, UserEntity user) {
        if (ValueTool.isEmpty(poi.getWorkflowStateId()))
            return true;
        WorkflowState state = poi.getWorkflowState();
        String[] visibleForRole = state.getVisibleForRole();
        if (visibleForRole == null)
            return true;

        if (poi.getUserIdList().contains(user.getId())) {
            // FIXME: to ze to na me assignovane, neznamena, ze to vidim
            return true;
        }
        String[] editableForRole = state.getEditableForRole();
        if (editableForRole != null && isAuthorized(editableForRole, user))
            return true;
        return isAuthorized(new String[] {BACKEND, ADMIN}, user) || isAuthorized(visibleForRole, user);
    }

    public boolean isCanBeStartedBy(PoiEntity poi, UserEntity user) {
        Workflow workflow = poi.getWorkflow();
        if (workflow == null)
            return true;
        return  isAuthorized(workflow.getCanBeStartedBy(), user);
    }
}
