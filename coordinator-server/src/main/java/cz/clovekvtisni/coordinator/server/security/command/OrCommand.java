package cz.clovekvtisni.coordinator.server.security.command;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;

/**
 * Created with IntelliJ IDEA.
 * User: tomucha
 * Date: 05.05.13
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
public class OrCommand implements PermissionCommand {

    private PermissionCommand<CoordinatorEntity>[] commands;

    public OrCommand(PermissionCommand... commands) {
        this.commands = commands;
    }

    @Override
    public boolean isPermitted(CoordinatorEntity entity, String entityName) {
        for (PermissionCommand<CoordinatorEntity> command : commands) {
            if (command.isPermitted(entity, entityName)) return true;
        }
        return false;
    }
}
