package cz.clovekvtisni.coordinator.server.security.plugin;

import cz.clovekvtisni.coordinator.domain.config.PoiCategory;
import cz.clovekvtisni.coordinator.domain.config.RolePermission;
import cz.clovekvtisni.coordinator.domain.config.Workflow;
import cz.clovekvtisni.coordinator.domain.config.WorkflowState;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.security.command.AbstractAuthorizationCommand;
import cz.clovekvtisni.coordinator.server.security.command.PermissionCommand;
import cz.clovekvtisni.coordinator.server.security.permission.*;
import cz.clovekvtisni.coordinator.util.ValueTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@Component
public class PoiSecurityPlugin extends SecurityPlugin {

    private AuthorizationTool authorizationTool;

    private AppContext appContext;

    private CoordinatorConfig config;

    @Autowired
    public PoiSecurityPlugin(AuthorizationTool authorizationTool,
                             AppContext appContext,
                             CoordinatorConfig config
    ) {
        this.authorizationTool = authorizationTool;
        this.appContext = appContext;
        this.config = config;
    }

    @Override
    protected void register() {
        PermissionCommand<PoiEntity> canReadCommand = new CanReadCommand();
        PermissionCommand<PoiEntity> canUpdateCommand = new CanUpdateCommand();
        PermissionCommand<PoiEntity> canCreateCommand = new CanCreateCommand();
        PermissionCommand<PoiEntity> canDoTransitionCommand = new CanDoTransitionCommand();

        registerPermissionCommand(PoiEntity.class, ReadPermission.class, canReadCommand);
        registerPermissionCommand("poiEntity", ReadPermission.class, canReadCommand);
        registerPermissionCommand(PoiEntity.class, CreatePermission.class, canCreateCommand);
        registerPermissionCommand("poiEntity", CreatePermission.class, canCreateCommand);
        registerPermissionCommand(PoiEntity.class, UpdatePermission.class, canUpdateCommand);
        registerPermissionCommand("poiEntity", UpdatePermission.class, canUpdateCommand);

        registerPermissionCommand(PoiEntity.class, DeletePermission.class, canCreateCommand);
        registerPermissionCommand("poiEntity", DeletePermission.class, canCreateCommand);

        registerPermissionCommand(PoiEntity.class, TransitionPermission.class, canDoTransitionCommand);
    }

    private class CanReadCommand implements PermissionCommand<PoiEntity> {
        @Override
        public boolean isPermitted(PoiEntity entity, String entityName) {
            UserEntity loggedUser = appContext.getLoggedUser();
            if (loggedUser == null)
                return false;

            if (entity == null && entityName != null)
                return true;

            if (loggedUser.getOrganizationId() == null || !loggedUser.getOrganizationId().equals(entity.getOrganizationId()))
                return false;

            if (entity.isImportant())
                return true;

            if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_POI_IN_ORG))
                return true;

            if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.TRANS_POI_ASSIGNED) && entity.getUserIdList() != null) {
                if (entity.isAssigned(loggedUser))
                    return true;
            }

            String[] rolesWithReadPermission = authorizationTool.findRolesWithReadPermission(entity).toArray(new String[0]);
            UserInEventEntity activeUserInEvent = appContext.getActiveUserInEvent();
            return
                loggedUser.hasAnyRole(rolesWithReadPermission) ||
                (activeUserInEvent != null && activeUserInEvent.hasAnyRole(rolesWithReadPermission));
        }
    }

    private class CanCreateCommand implements PermissionCommand<PoiEntity> {
        @Override
        public boolean isPermitted(PoiEntity entity, String entityName) {
            UserEntity loggedUser = appContext.getLoggedUser();
            if (loggedUser == null)
                return false;

            if (entity == null && entityName != null)
                return authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_POI_IN_ORG);

            if (!authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_POI_IN_ORG) ||
                loggedUser.getOrganizationId() == null ||
                !loggedUser.getOrganizationId().equals(entity.getOrganizationId())) {
                    return false;
            }

            // check permission in workflow settings
            PoiCategory c = config.getPoiCategoryMap().get(entity.getPoiCategoryId());
            Workflow w = config.getWorkflowMap().get(c.getWorkflowId());
            entity.setWorkflow(w);
            if (w != null && w.getCanBeStartedBy() != null) {
                UserInEventEntity activeUserInEvent = appContext.getActiveUserInEvent();
                if (
                        !loggedUser.hasAnyRole(w.getCanBeStartedBy()) &&
                        (
                                activeUserInEvent == null ||
                                !activeUserInEvent.getEventId().equals(entity.getEventId()) ||
                                !activeUserInEvent.hasAnyRole(w.getCanBeStartedBy())
                        )
                )
                    return false;
            }

            return true;
        }
    }

    private class CanUpdateCommand implements PermissionCommand<PoiEntity> {
        @Override
        public boolean isPermitted(PoiEntity entity, String entityName) {
            UserEntity loggedUser = appContext.getLoggedUser();
            if (loggedUser == null) {
                log.info("Logged user is null");
                return false;
            }

            if (entity == null && entityName != null) {
                log.info("Entity is null");
                return true;
            }

            if (loggedUser.getOrganizationId() == null || !loggedUser.getOrganizationId().equals(entity.getOrganizationId())) {
                log.info("Organization "+loggedUser.getOrganizationId()+" != "+entity.getOrganizationId());
                return false;
            }

            if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_POI_IN_ORG)) {
                return true;
            }

            String workflowId = entity.getWorkflowId();
            if (workflowId != null) {
                Workflow workflow = config.getWorkflowMap().get(workflowId);
                String workflowStateId = entity.getWorkflowStateId();
                if (workflow != null && workflowStateId != null) {
                    WorkflowState workflowState = workflow.getStateMap().get(workflowStateId);
                    log.info("Editable for roles: "+ Arrays.toString(workflowState.getEditableForRole()));
                    String[] editableForRole = workflowState.getEditableForRole();
                    UserInEventEntity activeUserInEvent = appContext.getActiveUserInEvent();
                    if (
                        editableForRole != null &&
                        (
                            loggedUser.hasAnyRole(editableForRole) ||
                            (activeUserInEvent != null && activeUserInEvent.hasAnyRole(editableForRole))
                        )
                    )
                        return true;
                }
            }

            return false;
        }
    }

    private class CanDoTransitionCommand implements PermissionCommand<PoiEntity> {
        @Override
        public boolean isPermitted(PoiEntity entity, String entityName) {
            UserEntity loggedUser = appContext.getLoggedUser();
            if (loggedUser == null)
                return false;

            if (entity == null && entityName != null)
                return true;

            if (
                authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_POI_IN_ORG) &&
                loggedUser.getOrganizationId() != null &&
                loggedUser.getOrganizationId().equals(entity.getOrganizationId())
            ) {
                return true;
            }

            if (entity.isAssigned(loggedUser)) {
                if (authorizationTool.hasAnyPermission(loggedUser, RolePermission.TRANS_POI_ASSIGNED))
                    return true;
                UserInEventEntity activeUserInEvent = appContext.getActiveUserInEvent();
                if (
                    activeUserInEvent != null &&
                    activeUserInEvent.getEventId().equals(entity.getEventId()) &&
                    authorizationTool.hasAnyPermission(activeUserInEvent, RolePermission.TRANS_POI_ASSIGNED)
                )
                    return true;
            }

            return false;
        }
    }
}
