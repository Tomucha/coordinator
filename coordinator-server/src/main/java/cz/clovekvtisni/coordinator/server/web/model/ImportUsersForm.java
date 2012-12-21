package cz.clovekvtisni.coordinator.server.web.model;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 21.12.12
 */
public class ImportUsersForm {

    private Long eventId;

    private String organizationId;

    private String[] typ;

    private String[][] val;

    public String[] getTyp() {
        return typ;
    }

    public void setTyp(String[] typ) {
        this.typ = typ;
    }

    public String[][] getVal() {
        return val;
    }

    public void setVal(String[][] val) {
        this.val = val;
    }

    public int getRowCount() {
        return val != null ? val.length : 0;
    }

    public int getColCount() {
        return val != null && val[0] != null ? val[0].length : 0;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}
