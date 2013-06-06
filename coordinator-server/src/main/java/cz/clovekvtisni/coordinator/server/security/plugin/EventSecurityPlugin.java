package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.domain.config.RolePermission;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.security.command.IsSuperadminCommand;
import cz.clovekvtisni.coordinator.server.security.command.PermissionCommand;
import cz.clovekvtisni.coordinator.server.security.command.PermittedCommand;
import cz.clovekvtisni.coordinator.server.security.command.UserLoggedCommand;
import cz.clovekvtisni.coordinator.server.security.permission.CreatePermission;
import cz.clovekvtisni.coordinator.server.security.permission.DeletePermission;
import cz.clovekvtisni.coordinator.server.security.permission.ReadPermission;
import cz.clovekvtisni.coordinator.server.security.permission.UpdatePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventSecurityPlugin extends SecurityPlugin {

    private AuthorizationTool authorizationTool;

    private AppContext appContext;

    @Autowired
    public EventSecurityPlugin(AuthorizationTool authorizationTool, AppContext appContext) {
        this.authorizationTool = authorizationTool;
        this.appContext = appContext;
    }

    @Override
    protected void register() {
        PermissionCommand<EventEntity> permittedCommand = new PermittedCommand<EventEntity>();
        PermissionCommand<EventEntity> canCreateCommand = new CanCreateCommand();

        registerPermissionCommand(EventEntity.class, ReadPermission.class, permittedCommand);
        registerPermissionCommand("eventEntity", ReadPermission.class, permittedCommand);
        registerPermissionCommand(EventEntity.class, CreatePermission.class, canCreateCommand);
        registerPermissionCommand("eventEntity", CreatePermission.class, canCreateCommand);
        registerPermissionCommand(EventEntity.class, UpdatePermission.class, canCreateCommand);
        registerPermissionCommand("eventEntity", UpdatePermission.class, canCreateCommand);
        registerPermissionCommand(EventEntity.class, DeletePermission.class, canCreateCommand);
        registerPermissionCommand("eventEntity", DeletePermission.class, canCreateCommand);
    }

    private class CanCreateCommand implements PermissionCommand<EventEntity> {
        @Override
        public boolean isPermitted(EventEntity entity, String entityName) {
            UserEntity loggedUser = appContext.getLoggedUser();

            return loggedUser != null && authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_EVENT);
        }
    }
}
