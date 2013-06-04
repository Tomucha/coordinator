package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.domain.config.RolePermission;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.security.command.*;
import cz.clovekvtisni.coordinator.server.security.permission.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
        PermissionCommand<UserEntity> permittedCommand = new PermittedCommand<UserEntity>();
        SystemCallCommand<UserEntity> systemCallCommand = new SystemCallCommand<UserEntity>(appContext);
        PermissionCommand<UserEntity> userLoggedCommand = new UserLoggedCommand<UserEntity>(appContext);
        HasRoleCommand<UserEntity> isAdmin = new HasRoleCommand<UserEntity>(appContext, authorizationTool, Arrays.asList(new String[]{AuthorizationTool.BACKEND, AuthorizationTool.COORDINATOR}));
        HasRoleCommand<UserEntity> isSuperuser = new HasRoleCommand<UserEntity>(appContext, authorizationTool, Arrays.asList(new String[]{"SUPERADMIN"}));

        registerPermissionCommand(UserEntity.class, MassMailPermission.class, new OrCommand(isSuperuser, systemCallCommand));
        registerPermissionCommand("userEntity", MassMailPermission.class, new OrCommand(isSuperuser, systemCallCommand));

        registerPermissionCommand(UserEntity.class, ReadPermission.class, permittedCommand);
        registerPermissionCommand("userEntity", ReadPermission.class, permittedCommand);
        registerPermissionCommand(UserEntity.class, CreatePermission.class, isAdmin);
        registerPermissionCommand("userEntity", CreatePermission.class, isAdmin);
        registerPermissionCommand(UserEntity.class, UpdatePermission.class, isAdmin);
        registerPermissionCommand("userEntity", UpdatePermission.class, isAdmin);
        registerPermissionCommand(UserEntity.class, DeletePermission.class, isAdmin);
        registerPermissionCommand("userEntity", DeletePermission.class, isAdmin);
        registerPermissionCommand(UserEntity.class, ViewUsersPermission.class, isAdmin);
        registerPermissionCommand(UserEntity.class, LoginAdminPermission.class, new CanLoginAdmin());
    }

    private class CanLoginAdmin extends AbstractPermissionCommand<UserEntity> {
        @Override
        public boolean isPermitted(UserEntity entity, String entityName) {
            return authorizationTool.hasAnyPermission(entity, RolePermission.CAN_LOGIN);
        }
    }
}