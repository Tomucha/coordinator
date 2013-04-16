package cz.clovekvtisni.coordinator.server.domain;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;
import com.googlecode.objectify.impl.ref.StdRef;
import cz.clovekvtisni.coordinator.domain.RegistrationStatus;
import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.server.util.EntityTool;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Cache
@Entity(name = "UserInEvent")
public class UserInEventEntity extends AbstractPersistentEntity<UserInEvent, UserInEventEntity> {

    @Id
    private Long id;

    @Index
    private Long eventId;

    @Parent
    @JsonIgnore
    private Key<UserEntity> parentKey;

    @Index
    private Long userId;

    private boolean usesSmartphoneApp;

    private String memo;

    private Date validFrom;

    private Date validTo;

    private RegistrationStatus status;

    private Double lastLocationLatitude;

    private Double lastLocationLongitude;

    @Index
    private List<String> lastLocationGeoCells;

    private Long lastLocationPrecission;

    private Date lastLocationTimestamp;

    private Long lastPoiId;

    @Ignore
    private PoiEntity lastPoiEntity;

    private Date lastPoiDate;

    private Long[] groupIdList;

    @Load
    @JsonIgnore
    Ref<UserEntity> refToUser;

    @Ignore
    private EventEntity eventEntity;

    @Ignore
    private List<UserGroupEntity> groupEntities;


    public UserInEventEntity() {
    }

    @Override
    protected UserInEvent createTargetEntity() {
        return new UserInEvent();
    }

    @Override
    public UserInEvent buildTargetEntity() {
        UserInEvent inEvent = super.buildTargetEntity();
        if (eventEntity != null)
            inEvent.setEvent(eventEntity.buildTargetEntity());
        if (getUserEntity() != null)
            inEvent.setUser(getUserEntity().buildTargetEntity());
        if (groupEntities != null)
            inEvent.setGroups(new EntityTool().buildTargetEntities(groupEntities));

        return inEvent;
    }

    @OnSave
    public void countGeoCells() {
        if (lastLocationLatitude != null && lastLocationLongitude != null) {

            // Transform it to a point
            Point p = new Point(lastLocationLatitude, lastLocationLongitude);

            // Generates the list of GeoCells
            List<String> cells = GeocellManager.generateGeoCell(p);
            lastLocationGeoCells = cells;

        } else {
            lastLocationGeoCells = Collections.EMPTY_LIST;
        }
    }

    @Override
    @JsonIgnore
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        // FIXME: zoufalost
        return getCreatedDate() == null;
    }

    @Override
    @JsonIgnore
    public Key<UserInEventEntity> getKey() {
        return createKey(userId, eventId);
    }

    public static Key<UserInEventEntity> createKey(Long userId, Long eventId) {
        return Key.create(
                Key.create(UserEntity.class, userId), UserInEventEntity.class, eventId
        );
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
        this.id = eventId;
    }

    public boolean isUsesSmartphoneApp() {
        return usesSmartphoneApp;
    }

    public void setUsesSmartphoneApp(boolean usesSmartphoneApp) {
        this.usesSmartphoneApp = usesSmartphoneApp;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public Double getLastLocationLatitude() {
        return lastLocationLatitude;
    }

    public void setLastLocationLatitude(Double lastLocationLatitude) {
        this.lastLocationLatitude = lastLocationLatitude;
    }

    public Double getLastLocationLongitude() {
        return lastLocationLongitude;
    }

    public void setLastLocationLongitude(Double lastLocationLongitude) {
        this.lastLocationLongitude = lastLocationLongitude;
    }

    public Long getLastLocationPrecission() {
        return lastLocationPrecission;
    }

    public void setLastLocationPrecission(Long lastLocationPrecission) {
        this.lastLocationPrecission = lastLocationPrecission;
    }

    public Date getLastLocationTimestamp() {
        return lastLocationTimestamp;
    }

    public void setLastLocationTimestamp(Date lastLocationTimestamp) {
        this.lastLocationTimestamp = lastLocationTimestamp;
    }

    public Long getLastPoiId() {
        return lastPoiId;
    }

    public void setLastPoiId(Long lastPoiId) {
        this.lastPoiId = lastPoiId;
    }

    public Date getLastPoiDate() {
        return lastPoiDate;
    }

    public void setLastPoiDate(Date lastPoiDate) {
        this.lastPoiDate = lastPoiDate;
    }

    public Long[] getGroupIdList() {
        return groupIdList;
    }

    public void setGroupIdList(Long[] groupIdList) {
        this.groupIdList = groupIdList;
    }

    public EventEntity getEventEntity() {
        return eventEntity;
    }

    public void setEventEntity(EventEntity eventEntity) {
        this.eventEntity = eventEntity;
    }

    public UserEntity getUserEntity() {
        return refToUser.getValue();
    }

    public void setUserEntity(UserEntity userEntity) {
        this.refToUser = new StdRef<UserEntity>(userEntity.getKey(), userEntity);
    }

    @JsonIgnore
    public Key<UserEntity> getParentKey() {
        return parentKey;
    }

    @JsonIgnore
    public void setParentKey(Key<UserEntity> parentKey) {
        this.parentKey = parentKey;
        if (parentKey != null) {
            this.userId = parentKey.getId();
        }
    }

    public List<UserGroupEntity> getGroupEntities() {
        return groupEntities;
    }

    public void setGroupEntities(List<UserGroupEntity> groupEntities) {
        this.groupEntities = groupEntities;
    }


    public String[] getRoles() {
        Set<String> roles = new HashSet<String>();
        if (getUserEntity() != null && getUserEntity().getRoleIdList() != null) {
            for (String role : getUserEntity().getRoleIdList()) {
                roles.add(role);
            }
        }
        if (groupEntities != null) {
            for (UserGroupEntity group : groupEntities) {
                roles.add(group.getRoleId());
            }
        }
        return roles.toArray(new String[0]);
    }

    public List<String> getLastLocationGeoCells() {
        return lastLocationGeoCells;
    }

    public void setLastLocationGeoCells(List<String> lastLocationGeoCells) {
        this.lastLocationGeoCells = lastLocationGeoCells;
    }

    public PoiEntity getLastPoiEntity() {
        return lastPoiEntity;
    }

    public void setLastPoiEntity(PoiEntity lastPoiEntity) {
        this.lastPoiEntity = lastPoiEntity;
    }

    @Override
    public String toString() {
        return "UserInEventEntity{" +
                "userId=" + userId +
                ", eventId='" + eventId + '\'' +
                ", usesSmartphoneApp=" + usesSmartphoneApp +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                ", status='" + status + '\'' +
                ", lastPoiDate=" + lastPoiDate +
                ", groupIdList=" + (groupIdList == null ? null : Arrays.asList(groupIdList)) +
                ", lastPoiId=" + lastPoiId +
                '}';
    }
}
