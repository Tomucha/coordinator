package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.domain.config.Role;
import cz.clovekvtisni.coordinator.domain.config.RolePermission;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.security.command.HasRoleCommand;
import cz.clovekvtisni.coordinator.server.security.command.IsSuperadminCommand;
import cz.clovekvtisni.coordinator.server.security.command.PermissionCommand;
import cz.clovekvtisni.coordinator.server.security.command.PermittedCommand;
import cz.clovekvtisni.coordinator.server.security.permission.CreatePermission;
import cz.clovekvtisni.coordinator.server.security.permission.DeletePermission;
import cz.clovekvtisni.coordinator.server.security.permission.ReadPermission;
import cz.clovekvtisni.coordinator.server.security.permission.UpdatePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

@Component
public class OrganizationInEventSecurityPlugin extends SecurityPlugin {

    private AuthorizationTool authorizationTool;

    private AppContext appContext;

    @Autowired
    public OrganizationInEventSecurityPlugin(AuthorizationTool authorizationTool, AppContext appContext) {
        this.authorizationTool = authorizationTool;
        this.appContext = appContext;
    }

    @Override
    protected void register() {
        PermissionCommand<OrganizationInEventEntity> permittedCommand = new CanReadCommand();
        PermissionCommand<OrganizationInEventEntity> canCreateCommand = new CanCreateCommand();

        registerPermissionCommand(OrganizationInEventEntity.class, ReadPermission.class, permittedCommand);
        registerPermissionCommand("organizationInEventEntity", ReadPermission.class, permittedCommand);
        registerPermissionCommand(OrganizationInEventEntity.class, CreatePermission.class, canCreateCommand);
        registerPermissionCommand("organizationInEventEntity", CreatePermission.class, canCreateCommand);
        registerPermissionCommand(OrganizationInEventEntity.class, UpdatePermission.class, canCreateCommand);
        registerPermissionCommand("organizationInEventEntity", UpdatePermission.class, canCreateCommand);
        registerPermissionCommand(OrganizationInEventEntity.class, DeletePermission.class, canCreateCommand);
        registerPermissionCommand("organizationInEventEntity", DeletePermission.class, canCreateCommand);
    }

    private class CanCreateCommand implements PermissionCommand<OrganizationInEventEntity> {
        @Override
        public boolean isPermitted(OrganizationInEventEntity entity, String entityName) {
            UserEntity loggedUser = appContext.getLoggedUser();

            if (entity == null && entityName != null)
                return loggedUser != null && authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_EVENT, RolePermission.EDIT_EVENT_IN_ORG);

            if (loggedUser == null || loggedUser.getOrganizationId() == null)
                return false;

            if (!loggedUser.getOrganizationId().equals(entity.getOrganizationId()))
                return false;

            if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_EVENT_IN_ORG))
                return true;

            UserInEventEntity activeUserInEvent = appContext.getActiveUserInEvent();
            return activeUserInEvent != null && authorizationTool.hasAnyPermission(activeUserInEvent, RolePermission.EDIT_EVENT_IN_ORG);
        }
    }

    private class CanReadCommand implements PermissionCommand<OrganizationInEventEntity> {
        public boolean isPermitted(OrganizationInEventEntity entity, String entityName) {
            UserEntity loggedUser = appContext.getLoggedUser();

            if (entity == null && entityName != null) return true;

            if (entity.getDateClosed() == null || entity.getDateClosed().after(new Date())) return true;

            return authorizationTool.hasRole(AuthorizationTool.SUPERADMIN, loggedUser);
        }

    }

}
