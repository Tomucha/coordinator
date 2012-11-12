package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.tool.objectify.NoDeletedFilter;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
public class EventFilter extends NoDeletedFilter<EventEntity> {

    private String eventIdVal;

    private Operator eventIdOp = Operator.EQ;

    @Override
    public Class<EventEntity> getEntityClass() {
        return EventEntity.class;
    }

    public String getEventIdVal() {
        return eventIdVal;
    }

    public void setEventIdVal(String eventIdVal) {
        this.eventIdVal = eventIdVal;
    }

    public Operator getEventIdOp() {
        return eventIdOp;
    }

    public void setEventIdOp(Operator eventIdOp) {
        this.eventIdOp = eventIdOp;
    }
}
