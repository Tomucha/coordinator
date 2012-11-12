package cz.clovekvtisni.coordinator.server.security.plugin;


import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.SecurityTool;
import cz.clovekvtisni.coordinator.server.security.command.PermissionCommand;
import cz.clovekvtisni.coordinator.server.security.permission.Permission;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 5/25/11
 * Time: 9:30 PM
 */
public abstract class SecurityPlugin {

    protected SecurityTool securityTool;

    protected String registrarInfo;

    protected abstract void register();

    @Autowired
    protected AppContext appContext;

    @Autowired
    public void setSecurityTool(SecurityTool securityTool) {
        this.securityTool = securityTool;
        registrarInfo = getClass().getSimpleName();
        register();
    }

    @SuppressWarnings("unchecked")
    public <E extends CoordinatorEntity> void registerPermissionCommand(Class<? extends E> entityClass, Class<? extends Permission> permissionClass, PermissionCommand<E> command) {
        securityTool.registerPermissionCommand(entityClass, permissionClass, command, registrarInfo);
    }

    @SuppressWarnings("unchecked")
    public <E extends CoordinatorEntity> void registerPermissionCommand(String entityName, Class<? extends Permission> permissionClass, PermissionCommand<E> command) {
        securityTool.registerPermissionCommand(entityName, permissionClass, command, registrarInfo);
    }
}
