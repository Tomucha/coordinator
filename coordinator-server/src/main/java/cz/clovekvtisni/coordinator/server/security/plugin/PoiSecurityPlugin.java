package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.security.command.*;
import cz.clovekvtisni.coordinator.server.security.permission.CreatePermission;
import cz.clovekvtisni.coordinator.server.security.permission.DeletePermission;
import cz.clovekvtisni.coordinator.server.security.permission.ReadPermission;
import cz.clovekvtisni.coordinator.server.security.permission.UpdatePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class PoiSecurityPlugin extends SecurityPlugin {

    private AuthorizationTool authorizationTool;

    private AppContext appContext;

    @Autowired
    public PoiSecurityPlugin(AuthorizationTool authorizationTool, AppContext appContext) {
        this.authorizationTool = authorizationTool;
        this.appContext = appContext;
    }

    @Override
    protected void register() {
        PermissionCommand<PoiEntity> isVisibleCommand = new AbstractAuthorizationCommand<PoiEntity>(appContext, authorizationTool) {
            @Override
            public boolean isPermitted(PoiEntity entity, String entityName) {
                return entity == null || authorizationTool.isVisibleFor(entity, loggedUser());
            }
        };
        PermissionCommand<PoiEntity> isBackendCommand = new HasRoleCommand<PoiEntity>(appContext, authorizationTool, Arrays.asList(new String[]{AuthorizationTool.BACKEND}));
        PermissionCommand<PoiEntity> isLogged = new UserLoggedCommand<PoiEntity>(appContext);

        registerPermissionCommand(PoiEntity.class, ReadPermission.class, isVisibleCommand);
        registerPermissionCommand("poiEntity", ReadPermission.class, isVisibleCommand);
        registerPermissionCommand(PoiEntity.class, CreatePermission.class, isLogged);
        registerPermissionCommand("poiEntity", CreatePermission.class, isLogged);
        registerPermissionCommand(PoiEntity.class, UpdatePermission.class, isBackendCommand);
        registerPermissionCommand("poiEntity", UpdatePermission.class, isBackendCommand);
        registerPermissionCommand(PoiEntity.class, DeletePermission.class, isBackendCommand);
        registerPermissionCommand("poiEntity", DeletePermission.class, isBackendCommand);
    }

}
