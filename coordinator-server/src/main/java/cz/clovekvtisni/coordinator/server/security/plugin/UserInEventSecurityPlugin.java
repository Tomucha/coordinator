package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.security.command.AbstractPermissionCommand;
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
        PermissionCommand<UserInEventEntity> canReadCommand = new CanReadCommand();
        PermissionCommand<UserInEventEntity> isAdminCommand = new HasRoleCommand<UserInEventEntity>(appContext, authorizationTool, Arrays.asList(new String[]{AuthorizationTool.ADMIN}));

        registerPermissionCommand(UserInEventEntity.class, ReadPermission.class, canReadCommand);
        registerPermissionCommand("userInEventEntity", ReadPermission.class, canReadCommand);
        registerPermissionCommand(UserInEventEntity.class, CreatePermission.class, isAdminCommand);
        registerPermissionCommand("userInEventEntity", CreatePermission.class, isAdminCommand);
        registerPermissionCommand(UserInEventEntity.class, UpdatePermission.class, isAdminCommand);
        registerPermissionCommand("userInEventEntity", UpdatePermission.class, isAdminCommand);
        registerPermissionCommand(UserInEventEntity.class, DeletePermission.class, isAdminCommand);
        registerPermissionCommand("userInEventEntity", DeletePermission.class, isAdminCommand);
    }

    private class CanReadCommand implements PermissionCommand<UserInEventEntity> {
        @Override
        public boolean isPermitted(UserInEventEntity entity, String entityName) {
            UserEntity loggedUser = appContext.getLoggedUser();
            if (loggedUser == null)
                return false;

            // superadmin can read everything
            if (authorizationTool.hasRole(AuthorizationTool.SUPERADMIN, loggedUser))
                return true;

            // user entity type can read everyone logged user
            if (entity == null)
                return true;

            if (loggedUser.getId().equals(entity.getUserId()))
                return true;

            // admin can read everyone in his organization
            if (authorizationTool.isAuthorized(new String[]{AuthorizationTool.ADMIN, AuthorizationTool.BACKEND}, loggedUser)) {
                if (loggedUser.getOrganizationId() != null && loggedUser.getOrganizationId().equals(entity.getUserEntity().getOrganizationId()))
                    return true;
            }

            UserInEventEntity activeUserInEvent = appContext.getActiveUserInEvent();

            if (activeUserInEvent != null && activeUserInEvent.hasSameGroup(entity))
                return true;

            return false;
        }
    }

}
