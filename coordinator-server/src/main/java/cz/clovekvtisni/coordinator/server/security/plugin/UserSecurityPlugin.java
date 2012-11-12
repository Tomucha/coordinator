package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.command.PermissionCommand;
import cz.clovekvtisni.coordinator.server.security.command.PermittedCommand;
import cz.clovekvtisni.coordinator.server.security.command.UserLoggedCommand;
import cz.clovekvtisni.coordinator.server.security.permission.CreatePermission;
import cz.clovekvtisni.coordinator.server.security.permission.DeletePermission;
import cz.clovekvtisni.coordinator.server.security.permission.ReadPermission;
import cz.clovekvtisni.coordinator.server.security.permission.UpdatePermission;
import org.springframework.stereotype.Component;

@Component
public class UserSecurityPlugin extends SecurityPlugin {

    @Override
    protected void register() {
        //TODO: realni prava
        PermissionCommand<UserEntity> permittedCommand = new PermittedCommand<UserEntity>();
        PermissionCommand<UserEntity> userLoggedCommand = new UserLoggedCommand<UserEntity>();

        registerPermissionCommand(UserEntity.class, ReadPermission.class, permittedCommand);
        registerPermissionCommand(UserEntity.class, CreatePermission.class, userLoggedCommand);
        registerPermissionCommand(UserEntity.class, UpdatePermission.class, userLoggedCommand);
        registerPermissionCommand(UserEntity.class, DeletePermission.class, userLoggedCommand);
    }

}
