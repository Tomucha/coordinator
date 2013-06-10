package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.domain.config.RolePermission;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
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
        PermissionCommand<UserGroupEntity> canReadCommand = new CanReadCommand();
        PermissionCommand<UserGroupEntity> canEditCommand = new CanEditCommand();

        registerPermissionCommand(UserGroupEntity.class, ReadPermission.class, canReadCommand);
        registerPermissionCommand("userGroupEntity", ReadPermission.class, canReadCommand);
        registerPermissionCommand(UserGroupEntity.class, CreatePermission.class, canEditCommand);
        registerPermissionCommand("userGroupEntity", CreatePermission.class, canEditCommand);
        registerPermissionCommand(UserGroupEntity.class, UpdatePermission.class, canEditCommand);
        registerPermissionCommand("userGroupEntity", UpdatePermission.class, canEditCommand);
        registerPermissionCommand(UserGroupEntity.class, DeletePermission.class, canEditCommand);
        registerPermissionCommand("userGroupEntity", DeletePermission.class, canEditCommand);
    }

    private class CanReadCommand implements PermissionCommand<UserGroupEntity> {
        @Override
        public boolean isPermitted(UserGroupEntity entity, String entityName) {
            UserEntity loggedUser = appContext.getLoggedUser();
            if (loggedUser == null)
                return false;

            if (entity == null && entityName != null)
                return true;

            // user entity type can read everyone logged user
            // superadmin can read everything
            if (entity == null || loggedUser.isSuperadmin())
                return true;

            // owner can read entity
            if (loggedUser.getId().equals(entity.getId()))
                return true;

            // edit permissions
            if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_USER))
                return true;

            return loggedUser.getOrganizationId() != null && loggedUser.getOrganizationId().equals(entity.getOrganizationId());
        }
    }

    private class CanEditCommand implements PermissionCommand<UserGroupEntity> {

        @Override
        public boolean isPermitted(UserGroupEntity entity, String entityName) {
            UserEntity loggedUser = appContext.getLoggedUser();

            if (entity == null && entityName != null)
                return authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_USER, RolePermission.EDIT_USER_IN_ORG);

            if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_USER))
                return true;

            if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_USER_IN_ORG))
                return entity.getOrganizationId() != null && entity.getOrganizationId().equals(loggedUser.getOrganizationId());

            return false;
        }
    }
}
