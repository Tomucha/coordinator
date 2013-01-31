package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.*;
import cz.clovekvtisni.coordinator.domain.RegistrationStatus;
import cz.clovekvtisni.coordinator.domain.UserGroup;
import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.server.util.EntityTool;

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

    @Parent
    private Key<UserEntity> parentKey;

    @Index
    private Long userId;

    @Index
    private Long eventId;

    private boolean usesSmartphoneApp;

    private String memo;

    private Date validFrom;

    private Date validTo;

    private RegistrationStatus status;

    private Double lastLocationLatitude;

    private Double lastLocationLongitude;

    private Long lastLocationPrecission;

    private Date lastLocationTimestamp;

    private Long lastPoiId;

    private Date lastPoiDate;

    private Long[] groupIdList;

    @Ignore
    private EventEntity eventEntity;

    @Ignore
    private UserEntity userEntity;

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
        if (userEntity != null)
            inEvent.setUser(userEntity.buildTargetEntity());
        if (groupEntities != null)
            inEvent.setGroups(new EntityTool().buildTargetEntities(groupEntities));

        return inEvent;
    }

    @Override
    public Key<UserInEventEntity> getKey() {
        return Key.create(UserInEventEntity.class, id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public Key<UserEntity> getParentKey() {
        return parentKey;
    }

    public void setParentKey(Key<UserEntity> parentKey) {
        this.parentKey = parentKey;
    }

    public List<UserGroupEntity> getGroupEntities() {
        return groupEntities;
    }

    public void setGroupEntities(List<UserGroupEntity> groupEntities) {
        this.groupEntities = groupEntities;
    }


    public String[] getRoles() {
        Set<String> roles = new HashSet<String>();
        if (userEntity != null && userEntity.getRoleIdList() != null) {
            for (String role : userEntity.getRoleIdList()) {
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

    @Override
    public String toString() {
        return "UserInEventEntity{" +
                "id=" + id +
                ", userId=" + userId +
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
