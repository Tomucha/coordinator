package cz.clovekvtisni.coordinator.server.security;

import cz.clovekvtisni.coordinator.domain.config.Role;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
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

    public boolean isAuthorized(List<String> needOneOfRoles, List<String>... hasAllRoles) {
        Set<String> hasRoles = new HashSet<String>();
        for (List<String> roles : hasAllRoles) {
            for (String role : roles) {
                hasRoles.add(role);
                hasRoles.addAll(roleParentMap.get(role));
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
}
