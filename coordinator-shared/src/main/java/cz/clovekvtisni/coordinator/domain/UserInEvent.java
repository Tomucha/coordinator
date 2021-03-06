package cz.clovekvtisni.coordinator.domain;

import cz.clovekvtisni.coordinator.domain.config.PoiCategory;

import java.util.*;

public class UserInEvent extends AbstractModifiableEntity {

    private Long userId;

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

    private Event event;

    private User user;

    private List<UserGroup> groups;

    /**
     * Do kterych kategorii smi zapisovat?
     */
    private PoiCategory[] openedCategories;

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

    public void setGroupIdList(Long[] groups) {
        this.groupIdList = groups;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<UserGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<UserGroup> groups) {
        this.groups = groups;
    }

    public PoiCategory[] getOpenedCategories() {
        return openedCategories;
    }

    public void setOpenedCategories(PoiCategory[] openedCategories) {
        this.openedCategories = openedCategories;
    }

    @Override
    public String toString() {
        return "UserInEvent{" +
                "userId=" + userId +
                ", eventId='" + eventId + '\'' +
                ", usesSmartphoneApp=" + usesSmartphoneApp +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                ", status='" + status + '\'' +
                ", groups=" + (groups == null ? null : Arrays.asList(groups)) +
                '}';
    }
}
