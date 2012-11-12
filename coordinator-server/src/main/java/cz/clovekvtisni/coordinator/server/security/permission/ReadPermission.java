package cz.clovekvtisni.coordinator.server.security.permission;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;

public class ReadPermission extends Permission {

    private static final long serialVersionUID = -7575164011112591370L;

    protected ReadPermission() {
    }

    public ReadPermission(CoordinatorEntity entity) {
        super(entity);
    }

    public ReadPermission(String entityKindName) {
        super(entityKindName);
    }
}
