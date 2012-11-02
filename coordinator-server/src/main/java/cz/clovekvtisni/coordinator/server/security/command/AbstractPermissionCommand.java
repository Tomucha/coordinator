package cz.clovekvtisni.coordinator.server.security.command;


import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 9/9/11
 * Time: 6:33 PM
 */
public abstract class AbstractPermissionCommand<E extends CoordinatorEntity> implements PermissionCommand<E> {

    @Autowired
    private AppContext appContext;

    protected User loggedUser() {
        return appContext.getLoggedUser();
    }

}
