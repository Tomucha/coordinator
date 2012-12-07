package cz.clovekvtisni.coordinator.domain;

public class UserGroup extends AbstractModifiableEntity {

    private String eventId;

    private String organizationId;

    private String name;

    private String role;

    private Long createdBy;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "UserGroup{" +
                ", eventId='" + eventId + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", createdBy=" + createdBy +
                '}';
    }
}
