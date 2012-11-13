package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.server.domain.UserEntity;
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

    @Autowired
    private AuthorizationTool authorizationTool;

    @Override
    protected void register() {
        //TODO: realni prava
        PermissionCommand<UserEntity> permittedCommand = new PermittedCommand<UserEntity>();
        PermissionCommand<UserEntity> userLoggedCommand = new UserLoggedCommand<UserEntity>(appContext);
        HasRoleCommand<UserEntity> isAdmin = new HasRoleCommand<UserEntity>(appContext, authorizationTool, Arrays.asList(new String[]{"ADMIN"}));

        registerPermissionCommand(UserEntity.class, ReadPermission.class, permittedCommand);
        registerPermissionCommand(UserEntity.class, CreatePermission.class, userLoggedCommand);
        registerPermissionCommand(UserEntity.class, UpdatePermission.class, userLoggedCommand);
        registerPermissionCommand(UserEntity.class, DeletePermission.class, userLoggedCommand);
        registerPermissionCommand(UserEntity.class, ViewUsersPermission.class, isAdmin);
    }
}