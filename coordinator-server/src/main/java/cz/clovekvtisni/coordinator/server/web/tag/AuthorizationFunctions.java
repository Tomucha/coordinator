package cz.clovekvtisni.coordinator.server.web.tag;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.security.SecurityTool;
import cz.clovekvtisni.coordinator.server.security.permission.CreatePermission;
import cz.clovekvtisni.coordinator.server.security.permission.ReadPermission;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * TODO case insensitive
 */
@Component
public class AuthorizationFunctions {

    private static SecurityTool securityTool;

    private static AuthorizationTool authorizationTool;

    private static AppContext appContext;

    @Autowired
    public void setSecurityTool(SecurityTool securityTool) {
        AuthorizationFunctions.securityTool = securityTool;
    }

    @Autowired
    public void setAuthorizationTool(AuthorizationTool authorizationTool) {
        AuthorizationFunctions.authorizationTool = authorizationTool;
    }

    @Autowired
    public void setAppContext(AppContext appContext) {
        AuthorizationFunctions.appContext = appContext;
    }

    public static boolean canRead(String entityName) {
        return securityTool.check(new ReadPermission(entityName));
    }

    public static boolean canRead(CoordinatorEntity entity) {
        return securityTool.check(new ReadPermission(entity));
    }

    public static boolean canCreate(CoordinatorEntity entity) {
        return securityTool.check(new CreatePermission(entity));
    }

    public static boolean canCreate(String entityName) {
        return securityTool.check(new CreatePermission(entityName));
    }

    @Deprecated
    public static boolean isSuperAdmin() {
        return hasRole(AuthorizationTool.SUPERADMIN);
    }

    @Deprecated
    public static boolean isAdmin() {
        return hasRole(AuthorizationTool.ADMIN);
    }

    @Deprecated
    public static boolean isBackend() {
        return hasRole(AuthorizationTool.BACKEND);
    }

    @Deprecated
    public static boolean hasRole(String roleId) {
        UserEntity user = appContext.getLoggedUser();
        if (appContext == null )
            return false;
        if (user == null || user.getRoleIdList() == null) return false;
        return authorizationTool.isAuthorized(Arrays.asList(new String[] {roleId}), Arrays.asList(user.getRoleIdList()));
    }

    public static boolean isCanBeStartedBy(PoiEntity poi) {
        UserEntity user = appContext.getLoggedUser();
        if (appContext == null )
            return false;
        return authorizationTool.isCanBeStartedBy(poi, user);
    }
}
