package cz.clovekvtisni.coordinator.domain;

public class UserGroup extends AbstractModifiableEntity {

    private Long eventId;

    private String organizationId;

    private String name;

    private String roleId;

    private Long createdBy;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
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
                ", roleId='" + roleId + '\'' +
                ", createdBy=" + createdBy +
                '}';
    }
}
