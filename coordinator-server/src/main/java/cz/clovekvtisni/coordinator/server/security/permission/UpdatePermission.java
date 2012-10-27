package cz.clovekvtisni.coordinator.server.security.permission;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;

public class UpdatePermission extends Permission {

    private static final long serialVersionUID = -505975220006035999L;

    protected UpdatePermission() {
    }

    public UpdatePermission(CoordinatorEntity entity) {
        super(entity);
    }

    public UpdatePermission(String entityKindName) {
        super(entityKindName);
    }
}
