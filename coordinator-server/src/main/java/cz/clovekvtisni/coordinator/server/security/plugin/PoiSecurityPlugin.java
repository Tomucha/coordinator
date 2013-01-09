package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.security.command.HasRoleCommand;
import cz.clovekvtisni.coordinator.server.security.command.PermissionCommand;
import cz.clovekvtisni.coordinator.server.security.command.PermittedCommand;
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
        PermissionCommand<PoiEntity> permittedCommand = new PermittedCommand<PoiEntity>();
        PermissionCommand<PoiEntity> isBackendCommand = new HasRoleCommand<PoiEntity>(appContext, authorizationTool, Arrays.asList(new String[]{AuthorizationTool.BACKEND}));

        registerPermissionCommand(PoiEntity.class, ReadPermission.class, permittedCommand);
        registerPermissionCommand("poiEntity", ReadPermission.class, permittedCommand);
        registerPermissionCommand(PoiEntity.class, CreatePermission.class, isBackendCommand);
        registerPermissionCommand("poiEntity", CreatePermission.class, isBackendCommand);
        registerPermissionCommand(PoiEntity.class, UpdatePermission.class, isBackendCommand);
        registerPermissionCommand("poiEntity", UpdatePermission.class, isBackendCommand);
        registerPermissionCommand(PoiEntity.class, DeletePermission.class, isBackendCommand);
        registerPermissionCommand("poiEntity", DeletePermission.class, isBackendCommand);
    }

}
