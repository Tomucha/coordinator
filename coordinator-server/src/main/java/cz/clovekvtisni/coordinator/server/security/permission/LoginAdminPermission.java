package cz.clovekvtisni.coordinator.server.security.permission;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;

public class LoginAdminPermission extends Permission {

    public LoginAdminPermission(CoordinatorEntity entity) {
        super(entity);
    }

    public LoginAdminPermission(String entityKindName) {
        super(entityKindName);
    }
}
