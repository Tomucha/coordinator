package cz.clovekvtisni.coordinator.server.security.command;


import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;

import java.util.Arrays;

public class IsSuperadminCommand<E extends CoordinatorEntity> extends HasRoleCommand<E> {

    public IsSuperadminCommand(AppContext appContext, AuthorizationTool authorizationTool) {
        super(appContext, authorizationTool, Arrays.asList(new String[] {AuthorizationTool.SUPERADMIN}));
    }
}
