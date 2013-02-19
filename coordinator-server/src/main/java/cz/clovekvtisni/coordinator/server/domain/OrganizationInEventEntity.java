package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.*;
import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created with intelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Cache
@Entity(name = "OrganizationInEvent")
public class OrganizationInEventEntity extends AbstractPersistentEntity<OrganizationInEvent, OrganizationInEventEntity> {

    @Id
    private Long id;

    @Index
    @NotEmpty
    private String organizationId;

    @Index
    @NotNull
    private Long eventId;

    @NotEmpty
    private String name;

    private String description;

    private String operationalInfo;

    private String[] registrationEquipment;

    private String[] registrationSkills;

    @Index
    private Date datePublish;

    private Date dateClosedRegistration;

    @Index
    private Date dateClosed;

    @Ignore
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
        if (registrationEquipment == null) return new String[0];
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

    public EventEntity getEventEntity() {
        return eventEntity;
    }

    public void setEventEntity(EventEntity eventEntity) {
        this.eventEntity = eventEntity;
        if (eventEntity != null) {
            setEventId(eventEntity.getId());
        }
    }

    @Override
    public OrganizationInEvent buildTargetEntity() {
        OrganizationInEvent inEvent = super.buildTargetEntity();
        if (eventEntity != null)
            inEvent.setEvent(eventEntity.buildTargetEntity());
        return inEvent;
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
