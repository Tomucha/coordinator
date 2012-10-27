package cz.clovekvtisni.coordinator.server.security.permission;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 5/25/11
 * Time: 9:11 PM
 */
public class PasswordChangePermission extends Permission {

    private static final long serialVersionUID = -8101581134477639426L;

    protected PasswordChangePermission() {
    }

    public PasswordChangePermission(CoordinatorEntity entity) {
        super(entity);
    }

    public PasswordChangePermission(String entityKindName) {
        super(entityKindName);
    }
}
