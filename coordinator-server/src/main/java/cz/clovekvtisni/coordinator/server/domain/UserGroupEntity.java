package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;
import cz.clovekvtisni.coordinator.domain.UserGroup;

import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Unindexed
@Cached
@Entity(name = "UserGroup")
public class UserGroupEntity extends AbstractPersistentEntity<UserGroup, UserGroupEntity> {

    @Id
    private Long id;

    private Long groupId;

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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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
                ", groupId=" + groupId +
                ", eventId='" + eventId + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", createdBy=" + createdBy +
                '}';
    }
}
