package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
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
        PermissionCommand<OrganizationInEventEntity> permittedCommand = new PermittedCommand<OrganizationInEventEntity>();
        PermissionCommand<OrganizationInEventEntity> isAdminCommand = new HasRoleCommand<OrganizationInEventEntity>(appContext, authorizationTool, Arrays.asList(new String[]{AuthorizationTool.ADMIN}));

        registerPermissionCommand(OrganizationInEventEntity.class, ReadPermission.class, permittedCommand);
        registerPermissionCommand("organizationInEventEntity", ReadPermission.class, permittedCommand);
        registerPermissionCommand(OrganizationInEventEntity.class, CreatePermission.class, isAdminCommand);
        registerPermissionCommand("organizationInEventEntity", CreatePermission.class, isAdminCommand);
        registerPermissionCommand(OrganizationInEventEntity.class, UpdatePermission.class, isAdminCommand);
        registerPermissionCommand("organizationInEventEntity", UpdatePermission.class, isAdminCommand);
        registerPermissionCommand(OrganizationInEventEntity.class, DeletePermission.class, isAdminCommand);
        registerPermissionCommand("organizationInEventEntity", DeletePermission.class, isAdminCommand);
    }

}
