package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.security.command.IsSuperadminCommand;
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
        PermissionCommand<EventEntity> permittedCommand = new PermittedCommand<EventEntity>();
        PermissionCommand<EventEntity> isSuperadminCommand = new IsSuperadminCommand<EventEntity>(appContext);

        registerPermissionCommand(EventEntity.class, ReadPermission.class, permittedCommand);
        registerPermissionCommand("eventEntity", ReadPermission.class, permittedCommand);
        registerPermissionCommand(EventEntity.class, CreatePermission.class, isSuperadminCommand);
        registerPermissionCommand("eventEntity", CreatePermission.class, isSuperadminCommand);
        registerPermissionCommand(EventEntity.class, UpdatePermission.class, isSuperadminCommand);
        registerPermissionCommand("eventEntity", UpdatePermission.class, isSuperadminCommand);
        registerPermissionCommand(EventEntity.class, DeletePermission.class, isSuperadminCommand);
        registerPermissionCommand("eventEntity", DeletePermission.class, isSuperadminCommand);
    }

}
