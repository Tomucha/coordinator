package cz.clovekvtisni.coordinator.server.web.tag;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.security.SecurityTool;
import cz.clovekvtisni.coordinator.server.security.permission.CreatePermission;
import cz.clovekvtisni.coordinator.server.security.permission.ReadPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationFunctions {

    protected static SecurityTool securityTool;

    @Autowired
    public void setSecurityTool(SecurityTool securityTool) {
        AuthorizationFunctions.securityTool = securityTool;
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
}
