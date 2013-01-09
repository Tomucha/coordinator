package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.server.domain.UserGroupEntity;
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
public class UserGroupSecurityPlugin extends SecurityPlugin {

    private AuthorizationTool authorizationTool;

    private AppContext appContext;

    @Autowired
    public UserGroupSecurityPlugin(AuthorizationTool authorizationTool, AppContext appContext) {
        this.authorizationTool = authorizationTool;
        this.appContext = appContext;
    }

    @Override
    protected void register() {
        PermissionCommand<UserGroupEntity> permittedCommand = new PermittedCommand<UserGroupEntity>();
        PermissionCommand<UserGroupEntity> isBackendCommand = new HasRoleCommand<UserGroupEntity>(appContext, authorizationTool, Arrays.asList(new String[]{AuthorizationTool.BACKEND}));

        registerPermissionCommand(UserGroupEntity.class, ReadPermission.class, permittedCommand);
        registerPermissionCommand("userGroupEntity", ReadPermission.class, permittedCommand);
        registerPermissionCommand(UserGroupEntity.class, CreatePermission.class, isBackendCommand);
        registerPermissionCommand("userGroupEntity", CreatePermission.class, isBackendCommand);
        registerPermissionCommand(UserGroupEntity.class, UpdatePermission.class, isBackendCommand);
        registerPermissionCommand("userGroupEntity", UpdatePermission.class, isBackendCommand);
        registerPermissionCommand(UserGroupEntity.class, DeletePermission.class, isBackendCommand);
        registerPermissionCommand("userGroupEntity", DeletePermission.class, isBackendCommand);
    }

}
