package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
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
public class UserInEventSecurityPlugin extends SecurityPlugin {

    private AuthorizationTool authorizationTool;

    private AppContext appContext;

    @Autowired
    public UserInEventSecurityPlugin(AuthorizationTool authorizationTool, AppContext appContext) {
        this.authorizationTool = authorizationTool;
        this.appContext = appContext;
    }

    @Override
    protected void register() {
        PermissionCommand<UserInEventEntity> permittedCommand = new PermittedCommand<UserInEventEntity>();
        PermissionCommand<UserInEventEntity> isAdminCommand = new HasRoleCommand<UserInEventEntity>(appContext, authorizationTool, Arrays.asList(new String[]{AuthorizationTool.ADMIN}));

        registerPermissionCommand(UserInEventEntity.class, ReadPermission.class, permittedCommand);
        registerPermissionCommand("userInEventEntity", ReadPermission.class, permittedCommand);
        registerPermissionCommand(UserInEventEntity.class, CreatePermission.class, isAdminCommand);
        registerPermissionCommand("userInEventEntity", CreatePermission.class, isAdminCommand);
        registerPermissionCommand(UserInEventEntity.class, UpdatePermission.class, isAdminCommand);
        registerPermissionCommand("userInEventEntity", UpdatePermission.class, isAdminCommand);
        registerPermissionCommand(UserInEventEntity.class, DeletePermission.class, isAdminCommand);
        registerPermissionCommand("userInEventEntity", DeletePermission.class, isAdminCommand);
    }

}
