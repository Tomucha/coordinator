package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.NoDeletedFilter;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 22.11.12
 */
public class UserInEventFilter extends NoDeletedFilter<UserInEventEntity> {

    private Long userIdVal;

    private Operator userIdOp = Operator.EQ;

    private Long eventIdVal;

    private Operator eventIdOp = Operator.EQ;

    public Long getUserIdVal() {
        return userIdVal;
    }

    public void setUserIdVal(Long userIdVal) {
        this.userIdVal = userIdVal;
    }

    public Operator getUserIdOp() {
        return userIdOp;
    }

    public void setUserIdOp(Operator userIdOp) {
        this.userIdOp = userIdOp;
    }

    public Long getEventIdVal() {
        return eventIdVal;
    }

    public void setEventIdVal(Long eventIdVal) {
        this.eventIdVal = eventIdVal;
    }

    public Operator getEventIdOp() {
        return eventIdOp;
    }

    public void setEventIdOp(Operator eventIdOp) {
        this.eventIdOp = eventIdOp;
    }

    @Override
    public Class<UserInEventEntity> getEntityClass() {
        return UserInEventEntity.class;
    }
}
