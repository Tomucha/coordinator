package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.*;
import cz.clovekvtisni.coordinator.domain.PoiLog;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Cache
@Entity(name = "Activity")
public class ActivityEntity implements CoordinatorEntity<ActivityEntity> {

    public static enum ActivityType {
        CREATED_POI,
        MODIFIED,
        WORKFLOW_START,
        WORKFLOW_TRANSITION,
        ASSIGNED,
        UNASSIGNED,
        DELETED
    }

    @Id
    private Long id;

    @Index
    private Long poiId;

    @Index
    private Long eventId;

    @Index
    private Long userId;

    private ActivityType type;

    private String comment;

    private String[] params;

    @Index
    private Date changeDate;

    @Index
    private Long changedBy;

    @Ignore
    private PoiEntity poiEntity;

    @Ignore
    private UserEntity userEntity;

    public ActivityEntity() {
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public Long getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(Long changedBy) {
        this.changedBy = changedBy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Key<ActivityEntity> getKey() {
        return Key.create(ActivityEntity.class, getId().longValue());
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public Long getPoiId() {
        return poiId;
    }

    public void setPoiId(Long poiId) {
        this.poiId = poiId;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PoiEntity getPoiEntity() {
        return poiEntity;
    }

    public void setPoiEntity(PoiEntity poiEntity) {
        this.poiEntity = poiEntity;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}
