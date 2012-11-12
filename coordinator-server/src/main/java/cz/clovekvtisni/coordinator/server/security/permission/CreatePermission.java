package cz.clovekvtisni.coordinator.server.security.permission;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 5/25/11
 * Time: 9:11 PM
 */
public class CreatePermission extends Permission {

    private static final long serialVersionUID = -6451875571852551916L;

    protected CreatePermission() {
    }

    public CreatePermission(CoordinatorEntity entity) {
        super(entity);
    }

    public CreatePermission(String entityKindName) {
        super(entityKindName);
    }
}
