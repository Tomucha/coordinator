package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.domain.config.RolePermission;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.security.command.AbstractPermissionCommand;
import cz.clovekvtisni.coordinator.server.security.command.PermissionCommand;
import cz.clovekvtisni.coordinator.server.security.permission.CreatePermission;
import cz.clovekvtisni.coordinator.server.security.permission.DeletePermission;
import cz.clovekvtisni.coordinator.server.security.permission.ReadPermission;
import cz.clovekvtisni.coordinator.server.security.permission.UpdatePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        PermissionCommand<UserInEventEntity> canReadCommand = new CanReadCommand();
        PermissionCommand<UserInEventEntity> canEditCommand = new CanEditCommand();

        registerPermissionCommand(UserInEventEntity.class, ReadPermission.class, canReadCommand);
        registerPermissionCommand("userInEventEntity", ReadPermission.class, canReadCommand);
        registerPermissionCommand(UserInEventEntity.class, CreatePermission.class, canEditCommand);
        registerPermissionCommand("userInEventEntity", CreatePermission.class, canEditCommand);
        registerPermissionCommand(UserInEventEntity.class, UpdatePermission.class, canEditCommand);
        registerPermissionCommand("userInEventEntity", UpdatePermission.class, canEditCommand);
        registerPermissionCommand(UserInEventEntity.class, DeletePermission.class, canEditCommand);
        registerPermissionCommand("userInEventEntity", DeletePermission.class, canEditCommand);
    }

    private class CanReadCommand implements PermissionCommand<UserInEventEntity> {
        @Override
        public boolean isPermitted(UserInEventEntity entity, String entityName) {
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
            if (loggedUser.getId().equals(entity.getUserId()))
                return true;

            // read permissions
            if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.READ_USER, RolePermission.EDIT_USER))
                return true;

            if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.READ_USER_IN_GROUP, RolePermission.EDIT_USER_IN_ORG)) {
                if (loggedUser.getOrganizationId() != null && loggedUser.getOrganizationId().equals(entity.getUserEntity().getOrganizationId()))
                    return true;
            }

            // user can see other users in the same group
            UserInEventEntity activeUserInEvent = appContext.getActiveUserInEvent();

            if (activeUserInEvent != null && activeUserInEvent.hasSameGroup(entity))
                return true;

            return false;
        }
    }

    private class CanEditCommand implements PermissionCommand<UserInEventEntity> {
        @Override
        public boolean isPermitted(UserInEventEntity entity, String entityName) {
            UserEntity loggedUser = appContext.getLoggedUser();

            if (entity == null && entityName != null)
                return authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_USER, RolePermission.EDIT_USER_IN_ORG);

            if (loggedUser.equals(entity.getUserEntity()))
                return true;

            if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_USER))
                return true;

            UserEntity user = entity.getUserEntity();
            if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_USER_IN_ORG))
                return user.getOrganizationId() != null && user.getOrganizationId().equals(loggedUser.getOrganizationId());

            return false;
        }
    }
}
