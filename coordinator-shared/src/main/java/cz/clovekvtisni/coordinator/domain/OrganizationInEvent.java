package cz.clovekvtisni.coordinator.domain;

import java.util.Date;

public class OrganizationInEvent extends AbstractModifiableEntity {

    private String organizationId;

    private Long eventId;

    private String name;

    private String description;

    private String operationalInfo;

    private String[] registrationEquipment;

    private String[] registrationSkills;

    private Date datePublish;

    private Date dateClosedRegistration;

    private Date dateClosed;

    private Event event;

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperationalInfo() {
        return operationalInfo;
    }

    public void setOperationalInfo(String operationalInfo) {
        this.operationalInfo = operationalInfo;
    }

    public String[] getRegistrationEquipment() {
        return registrationEquipment;
    }

    public void setRegistrationEquipment(String[] registrationEquipment) {
        this.registrationEquipment = registrationEquipment;
    }

    public String[] getRegistrationSkills() {
        return registrationSkills;
    }

    public void setRegistrationSkills(String[] registrationSkills) {
        this.registrationSkills = registrationSkills;
    }

    public Date getDatePublish() {
        return datePublish;
    }

    public void setDatePublish(Date datePublish) {
        this.datePublish = datePublish;
    }

    public Date getDateClosedRegistration() {
        return dateClosedRegistration;
    }

    public void setDateClosedRegistration(Date dateClosedRegistration) {
        this.dateClosedRegistration = dateClosedRegistration;
    }

    public Date getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(Date dateClosed) {
        this.dateClosed = dateClosed;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public boolean registrationPossible() {
        if (datePublish == null && dateClosedRegistration == null) return true;
        if (datePublish == null) {
            if (dateClosedRegistration.getTime() > System.currentTimeMillis()) return true;
            return false;
        }
        if (dateClosedRegistration == null) {
            if (datePublish.getTime() < System.currentTimeMillis()) return true;
            return false;
        }
        return false;
    }

    @Override
    public String toString() {
        return "OrganizationInEvent{" +
                "organizationId='" + organizationId + '\'' +
                ", eventId='" + eventId + '\'' +
                ", name='" + name + '\'' +
                ", operationalInfo='" + operationalInfo + '\'' +
                ", registrationEquipment=" + registrationEquipment +
                ", registrationSkills=" + registrationSkills +
                ", datePublish=" + datePublish +
                ", dateClosedRegistration=" + dateClosedRegistration +
                ", dateClosed=" + dateClosed +
                '}';
    }
}
