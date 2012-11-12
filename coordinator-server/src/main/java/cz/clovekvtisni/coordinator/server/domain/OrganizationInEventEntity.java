package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;
import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Unindexed
@Cached
@Entity(name = "OrganizationInEvent")
public class OrganizationInEventEntity extends AbstractPersistentEntity<OrganizationInEvent, OrganizationInEventEntity> {

    @Id
    private Long id;

    @Indexed
    private String organizationId;

    @Indexed
    private String eventId;

    private String name;

    private String description;

    private String operationalInfo;

    private List<String> registrationEquipment;

    private List<String> registrationSkills;

    private Date datePublish;

    private Date dateClosedRegistration;

    private Date dateClosed;

    @Transient
    private EventEntity eventEntity;

    public OrganizationInEventEntity() {
    }

    @Override
    protected OrganizationInEvent createTargetEntity() {
        return new OrganizationInEvent();
    }

    @Override
    public Key<OrganizationInEventEntity> getKey() {
        return Key.create(OrganizationInEventEntity.class, id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
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

    public List<String> getRegistrationEquipment() {
        return registrationEquipment;
    }

    public void setRegistrationEquipment(List<String> registrationEquipment) {
        this.registrationEquipment = registrationEquipment;
    }

    public List<String> getRegistrationSkills() {
        return registrationSkills;
    }

    public void setRegistrationSkills(List<String> registrationSkills) {
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

    public EventEntity getEventEntity() {
        return eventEntity;
    }

    public void setEventEntity(EventEntity eventEntity) {
        this.eventEntity = eventEntity;
    }

    @Override
    public String toString() {
        return "OrganizationInEventEntity{" +
                "id=" + id +
                ", organizationId='" + organizationId + '\'' +
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
