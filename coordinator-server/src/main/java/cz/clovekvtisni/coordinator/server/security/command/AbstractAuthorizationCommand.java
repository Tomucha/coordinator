package cz.clovekvtisni.coordinator.server.security.command;


import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractAuthorizationCommand<E extends CoordinatorEntity> extends AbstractPermissionCommand<E> {

    private AuthorizationTool authorizationTool;

    public AbstractAuthorizationCommand(AppContext appContext, AuthorizationTool authorizationTool) {
        super(appContext);
        this.authorizationTool = authorizationTool;
    }
}
