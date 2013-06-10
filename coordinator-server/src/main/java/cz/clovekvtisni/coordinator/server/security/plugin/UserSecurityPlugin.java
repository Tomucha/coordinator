package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.domain.config.RolePermission;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.security.command.*;
import cz.clovekvtisni.coordinator.server.security.permission.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        PermissionCommand<UserEntity> canReadCommand = new CanReadCommand();
        SystemCallCommand<UserEntity> systemCallCommand = new SystemCallCommand<UserEntity>(appContext);
        PermissionCommand<UserEntity> isSuperuser = new IsSuperadminCommand<UserEntity>(appContext);

        registerPermissionCommand(UserEntity.class, MassMailPermission.class, new OrCommand(isSuperuser, systemCallCommand));
        registerPermissionCommand("userEntity", MassMailPermission.class, new OrCommand(isSuperuser, systemCallCommand));

        registerPermissionCommand(UserEntity.class, ReadPermission.class, canReadCommand);
        registerPermissionCommand("userEntity", ReadPermission.class, canReadCommand);

        CanEditUserCommand canEditCommand = new CanEditUserCommand();
        registerPermissionCommand(UserEntity.class, CreatePermission.class, canEditCommand);
        registerPermissionCommand("userEntity", CreatePermission.class, canEditCommand);
        registerPermissionCommand(UserEntity.class, UpdatePermission.class, canEditCommand);
        registerPermissionCommand("userEntity", UpdatePermission.class, canEditCommand);
        registerPermissionCommand(UserEntity.class, DeletePermission.class, canEditCommand);
        registerPermissionCommand("userEntity", DeletePermission.class, canEditCommand);
        registerPermissionCommand(UserEntity.class, ViewUsersPermission.class, canEditCommand);
        registerPermissionCommand(UserEntity.class, LoginAdminPermission.class, new CanLoginAdminCommand());
    }

    private class CanLoginAdminCommand extends AbstractPermissionCommand<UserEntity> {
        @Override
        public boolean isPermitted(UserEntity entity, String entityName) {
            return authorizationTool.hasAnyPermission(entity, RolePermission.CAN_LOGIN);
        }
    }

    private class CanReadCommand implements PermissionCommand<UserEntity> {
        @Override
        public boolean isPermitted(UserEntity entity, String entityName) {
            UserEntity loggedUser = appContext.getLoggedUser();
            if (loggedUser == null)
                return false;

            if (entity == null && entityName != null)
                return authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_USER, RolePermission.EDIT_USER_IN_ORG);

            // user entity type can read everyone logged user
            // superadmin can read everything
            if (entity == null || loggedUser.isSuperadmin())
                return true;

            // owner can read entity
            if (loggedUser.getId().equals(entity.getId()))
                return true;

            // read permissions
            if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_USER))
                return true;

            if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_USER_IN_ORG)) {
                if (loggedUser.getOrganizationId() != null && loggedUser.getOrganizationId().equals(entity.getOrganizationId()))
                    return true;
            }

            return false;
        }
    }

    private class CanEditUserCommand implements PermissionCommand<UserEntity> {

        @Override
        public boolean isPermitted(UserEntity entity, String entityName) {
            UserEntity loggedUser = appContext.getLoggedUser();

            if (entity == null && entityName != null)
                return authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_USER, RolePermission.EDIT_USER_IN_ORG);

            if (loggedUser.equals(entity))
                return true;

            if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_USER))
                return true;

            if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_USER_IN_ORG))
                return entity.getOrganizationId() != null && entity.getOrganizationId().equals(loggedUser.getOrganizationId());

            return false;
        }
    }
}