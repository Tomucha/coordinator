package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import cz.clovekvtisni.coordinator.domain.UserGroup;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Cache
@Entity(name = "UserGroup")
public class UserGroupEntity extends AbstractPersistentEntity<UserGroup, UserGroupEntity> {

    @Id
    private Long id;

    private String eventId;

    private String organizationId;

    private String name;

    private String role;

    private Long createdBy;

    public UserGroupEntity() {
    }

    @Override
    protected UserGroup createTargetEntity() {
        return new UserGroup();
    }

    @Override
    public Key<UserGroupEntity> getKey() {
        return Key.create(UserGroupEntity.class, id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        return "UserGroupEntity{" +
                "id=" + id +
                ", eventId='" + eventId + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", createdBy=" + createdBy +
                '}';
    }
}
