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

    private List<String> typ;

    private List<List<String>> val;

    private List<Integer> checked;

    public List<String> getTyp() {
        return typ;
    }

    public void setTyp(List<String> typ) {
        this.typ = typ;
    }

    public List<List<String>> getVal() {
        return val;
    }

    public void setVal(List<List<String>> val) {
        this.val = val;
    }

    public int getRowCount() {
        return val != null ? val.size() : 0;
    }

    public int getColCount() {
        return val != null && val.get(0) != null ? val.get(0).size() : 0;
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

    public List<Integer> getChecked() {
        return checked;
    }

    public void setChecked(List<Integer> checked) {
        this.checked = checked;
    }
}
