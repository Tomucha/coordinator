package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.security.command.HasRoleCommand;
import cz.clovekvtisni.coordinator.server.security.command.PermissionCommand;
import cz.clovekvtisni.coordinator.server.security.command.PermittedCommand;
import cz.clovekvtisni.coordinator.server.security.command.UserLoggedCommand;
import cz.clovekvtisni.coordinator.server.security.permission.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class UserSecurityPlugin extends SecurityPlugin {

    private AuthorizationTool authorizationTool;

    private AppContext appContext;

    @Autowired
    public UserSecurityPlugin(AuthorizationTool authorizationTool, AppContext appContext) {
        this.authorizationTool = authorizationTool;
        this.appContext = appContext;
    }

    @Override
    protected void register() {
        //TODO: realni prava
        PermissionCommand<UserEntity> permittedCommand = new PermittedCommand<UserEntity>();
        PermissionCommand<UserEntity> userLoggedCommand = new UserLoggedCommand<UserEntity>(appContext);
        HasRoleCommand<UserEntity> isAdmin = new HasRoleCommand<UserEntity>(appContext, authorizationTool, Arrays.asList(new String[]{"BACKEND"}));

        registerPermissionCommand(UserEntity.class, ReadPermission.class, permittedCommand);
        registerPermissionCommand("userEntity", ReadPermission.class, permittedCommand);
        registerPermissionCommand(UserEntity.class, CreatePermission.class, isAdmin);
        registerPermissionCommand("userEntity", CreatePermission.class, isAdmin);
        registerPermissionCommand(UserEntity.class, UpdatePermission.class, isAdmin);
        registerPermissionCommand("userEntity", UpdatePermission.class, isAdmin);
        registerPermissionCommand(UserEntity.class, DeletePermission.class, isAdmin);
        registerPermissionCommand("userEntity", DeletePermission.class, isAdmin);
        registerPermissionCommand(UserEntity.class, ViewUsersPermission.class, isAdmin);
    }
}