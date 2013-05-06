package cz.clovekvtisni.coordinator.server.workflow.callback;

import cz.clovekvtisni.coordinator.domain.config.WorkflowTransition;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.workflow.WorkflowCallbackAccessor;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class WorkflowCallback {

    @Autowired
    protected AppContext appContext;

    public boolean onBeforeTransition(PoiEntity poiEntity, WorkflowTransition transition) {
        return true;
    }

    @Autowired
    public void setWorkflowCallbackAccessor(WorkflowCallbackAccessor accessor) {
        accessor.register(this);
    }

    protected UserEntity getLoggedUser() {
        return appContext != null ? appContext.getLoggedUser() : null;
    }
}
