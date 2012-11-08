package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 8.11.12
 */
public class OrganizationInEventFilter extends Filter {

    private String organizationIdVal;

    private Filter.Operator organizationIdOp = Filter.Operator.EQ;

    private String eventIdVal;

    private Filter.Operator eventIdOp = Filter.Operator.EQ;

    public String getOrganizationIdVal() {
        return organizationIdVal;
    }

    public void setOrganizationIdVal(String organizationIdVal) {
        this.organizationIdVal = organizationIdVal;
    }

    public Operator getOrganizationIdOp() {
        return organizationIdOp;
    }

    public void setOrganizationIdOp(Operator organizationIdOp) {
        this.organizationIdOp = organizationIdOp;
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
