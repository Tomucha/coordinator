package cz.clovekvtisni.coordinator.server.security.permission;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 5/25/11
 * Time: 9:11 PM
 */
public class ViewUsersPermission extends Permission {

    protected ViewUsersPermission() {
    }

    public ViewUsersPermission(CoordinatorEntity entity) {
        super(entity);
    }

    public ViewUsersPermission(String entityKindName) {
        super(entityKindName);
    }
}
