package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.server.domain.UserEquipmentEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
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
public class UseEquipmentSecurityPlugin extends SecurityPlugin {

    @Autowired
    private AppContext appContext;

    @Override
    protected void register() {
        //TODO: realna prava
        PermissionCommand<UserEquipmentEntity> permittedCommand = new PermittedCommand<UserEquipmentEntity>();
        PermissionCommand<UserEquipmentEntity> userLoggedCommand = new UserLoggedCommand<UserEquipmentEntity>(appContext);

        registerPermissionCommand(UserEquipmentEntity.class, ReadPermission.class, permittedCommand);
        registerPermissionCommand(UserEquipmentEntity.class, CreatePermission.class, userLoggedCommand);
        registerPermissionCommand(UserEquipmentEntity.class, UpdatePermission.class, userLoggedCommand);
        registerPermissionCommand(UserEquipmentEntity.class, DeletePermission.class, userLoggedCommand);
    }
}
