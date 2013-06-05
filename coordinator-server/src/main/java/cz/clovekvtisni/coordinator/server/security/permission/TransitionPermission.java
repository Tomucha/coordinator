package cz.clovekvtisni.coordinator.server.security.permission;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;

public class TransitionPermission extends Permission {

    private String transitionId;

    public TransitionPermission() {
    }

    public TransitionPermission(CoordinatorEntity entity) {
        super(entity);
    }

    public TransitionPermission(CoordinatorEntity entity, String transitionId) {
        super(entity);
        this.transitionId = transitionId;
    }

    public TransitionPermission(String entityKindName) {
        super(entityKindName);
    }

    public String getTransitionId() {
        return transitionId;
    }
}
