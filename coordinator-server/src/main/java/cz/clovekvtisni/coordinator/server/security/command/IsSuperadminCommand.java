package cz.clovekvtisni.coordinator.server.security.command;


import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 9/7/11
 * Time: 12:52 PM
 */
public class IsSuperadminCommand<E extends CoordinatorEntity> extends AbstractPermissionCommand<E> {

    public IsSuperadminCommand(AppContext appContext) {
        super(appContext);
    }

    @Override
    public boolean isPermitted(E entity, String entityName) {
        UserEntity user = loggedUser();
        return user != null &&
                user.getRoleIdList() != null &&
                Arrays.asList(user.getRoleIdList()).contains("SUPERADMIN");
    }
}
