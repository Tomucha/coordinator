package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.domain.EventLocation;
import cz.clovekvtisni.coordinator.server.domain.EventLocationEntity;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.NoDeletedFilter;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 8.11.12
 */
public class EventLocationFilter extends NoDeletedFilter<EventLocationEntity> {

    private String eventIdVal;

    private Operator eventIdOp = Operator.EQ;

    @Override
    public Class<EventLocationEntity> getEntityClass() {
        return EventLocationEntity.class;
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
