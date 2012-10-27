package cz.clovekvtisni.coordinator.server.security.permission;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 5/25/11
 * Time: 9:11 PM
 */
public class DeletePermission extends Permission {

    private static final long serialVersionUID = 3483301871561470343L;

    protected DeletePermission() {
    }

    public DeletePermission(CoordinatorEntity entity) {
        super(entity);
    }

    public DeletePermission(String entityKindName) {
        super(entityKindName);
    }
}
