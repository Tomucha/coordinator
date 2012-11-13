package cz.clovekvtisni.coordinator.server.security.command;


import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 9/7/11
 * Time: 12:52 PM
 */
public class HasRoleCommand<E extends CoordinatorEntity> extends AbstractPermissionCommand<E> {

    private AuthorizationTool authorizationTool;

    private List<String> needRoles;

    public HasRoleCommand(AppContext appContext, AuthorizationTool authorizationTool, List<String> needRoles) {
        super(appContext);
        this.authorizationTool = authorizationTool;
        this.needRoles = needRoles;
    }

    @Override
    public boolean isPermitted(E entity, String entityName) {
        UserEntity user = loggedUser();
        return user != null &&
                user.getRoleIdList() != null &&
                authorizationTool.isAuthorized(needRoles, Arrays.asList(user.getRoleIdList()));
    }
}
