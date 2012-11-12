package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 8.11.12
 */
public class EventLocationFilter extends Filter {

    private String eventIdVal;

    private Operator eventIdOp = Operator.EQ;

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
