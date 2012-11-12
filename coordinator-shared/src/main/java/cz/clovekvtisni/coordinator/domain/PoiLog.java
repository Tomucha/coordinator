package cz.clovekvtisni.coordinator.domain;

import java.util.Date;

public class PoiLog extends AbstractModifiableEntity {

    private Long poiId;

    private Date changeDate;

    private String change;

    private String fromStateId;

    private String toStateId;

    private Long changedBy;

    public Long getPoiId() {
        return poiId;
    }

    public void setPoiId(Long poiId) {
        this.poiId = poiId;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getFromStateId() {
        return fromStateId;
    }

    public void setFromStateId(String fromStateId) {
        this.fromStateId = fromStateId;
    }

    public String getToStateId() {
        return toStateId;
    }

    public void setToStateId(String toStateId) {
        this.toStateId = toStateId;
    }

    public Long getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(Long changedBy) {
        this.changedBy = changedBy;
    }

    @Override
    public String toString() {
        return "PoiLog{" +
                "poiId=" + poiId +
                ", changeDate=" + changeDate +
                ", change='" + change + '\'' +
                ", fromStateId='" + fromStateId + '\'' +
                ", toStateId='" + toStateId + '\'' +
                ", changedBy=" + changedBy +
                '}';
    }
}
