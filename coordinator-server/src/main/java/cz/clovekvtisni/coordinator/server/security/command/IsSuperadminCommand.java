package cz.clovekvtisni.coordinator.server.security.command;


import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;

import java.util.Arrays;

public class IsSuperadminCommand<E extends CoordinatorEntity> extends AbstractPermissionCommand<UserEntity> {

    public IsSuperadminCommand(AppContext appContext) {
        super(appContext);
    }

    @Override
    public boolean isPermitted(UserEntity entity, String entityName) {
        return loggedUser() != null && loggedUser().isSuperadmin();
    }
}
