package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;
import cz.clovekvtisni.coordinator.domain.PoiLog;

import javax.persistence.Id;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Unindexed
@Cached
@Entity(name = "PoiLog")
public class PoiLogEntity extends AbstractPersistentEntity<PoiLog, PoiLogEntity> {

    @Id
    private Long id;

    private Long poiId;

    private Date changeDate;

    private String change;

    private String fromStateId;

    private String toStateId;

    private Long changedBy;

    public PoiLogEntity() {
    }

    @Override
    protected PoiLog createTargetEntity() {
        return new PoiLog();
    }

    @Override
    public Key<PoiLogEntity> getKey() {
        return Key.create(PoiLogEntity.class, id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        return "PoiLogEntity{" +
                "id=" + id +
                ", poiId=" + poiId +
                ", changeDate=" + changeDate +
                ", change='" + change + '\'' +
                ", fromStateId='" + fromStateId + '\'' +
                ", toStateId='" + toStateId + '\'' +
                ", changedBy=" + changedBy +
                '}';
    }
}
