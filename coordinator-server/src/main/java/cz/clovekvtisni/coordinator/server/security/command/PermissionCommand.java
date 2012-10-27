package cz.clovekvtisni.coordinator.server.security.command;


import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 5/26/11
 * Time: 9:04 AM
 */
public interface PermissionCommand<E extends CoordinatorEntity> {
    public boolean isPermitted(E entity, String entityName);
}
