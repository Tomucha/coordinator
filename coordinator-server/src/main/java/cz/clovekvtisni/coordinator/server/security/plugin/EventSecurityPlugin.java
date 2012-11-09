package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.security.command.PermissionCommand;
import cz.clovekvtisni.coordinator.server.security.command.PermittedCommand;
import cz.clovekvtisni.coordinator.server.security.command.UserLoggedCommand;
import cz.clovekvtisni.coordinator.server.security.permission.CreatePermission;
import cz.clovekvtisni.coordinator.server.security.permission.DeletePermission;
import cz.clovekvtisni.coordinator.server.security.permission.ReadPermission;
import cz.clovekvtisni.coordinator.server.security.permission.UpdatePermission;
import org.springframework.stereotype.Component;

@Component
public class EventSecurityPlugin extends SecurityPlugin {

    @Override
    protected void register() {
        //TODO: realni prava
        PermissionCommand<EventEntity> permittedCommand = new PermittedCommand<EventEntity>();
        PermissionCommand<EventEntity> userLoggedCommand = new UserLoggedCommand<EventEntity>();

        registerPermissionCommand(EventEntity.class, ReadPermission.class, permittedCommand);
        registerPermissionCommand(EventEntity.class, CreatePermission.class, userLoggedCommand);
        registerPermissionCommand(EventEntity.class, UpdatePermission.class, userLoggedCommand);
        registerPermissionCommand(EventEntity.class, DeletePermission.class, userLoggedCommand);
    }

}
