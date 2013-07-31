package cz.clovekvtisni.coordinator.server.web.model;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 21.12.12
 */
public class ImportUsersForm {

    public static final String EMPTY_TYPE = "0";

    private Long eventId;

    private String organizationId;

    private List<String> typ;

    @NotNull
    private String charset;

    @NotNull
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
        return val != null && val.size() > 0 && val.get(0) != null ? val.get(0).size() : 0;
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

    /** select 1th val on 1th column, 2nd val on 2nd colunn, etc. */
    public void preSelectTypes(Collection<String> strings) {
        List<String> l = new ArrayList<String>(strings.size());
        for (String v : strings)
            if (!EMPTY_TYPE.equals(v))
                l.add(v);

        List<String> vals = new ArrayList<String>(getColCount());
        for (int i = 0; i < getColCount() ; i++) {
            vals.add(i < l.size() ? l.get(i) : EMPTY_TYPE);
        }

        setTyp(vals);
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
