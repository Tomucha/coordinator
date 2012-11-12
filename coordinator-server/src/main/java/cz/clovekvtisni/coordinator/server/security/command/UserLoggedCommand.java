package cz.clovekvtisni.coordinator.server.security.command;


import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 9/7/11
 * Time: 12:52 PM
 */
public class UserLoggedCommand<E extends CoordinatorEntity> extends AbstractPermissionCommand<E> {

    @Override
    public boolean isPermitted(E entity, String entityName) {
        return loggedUser() != null;
    }
}
