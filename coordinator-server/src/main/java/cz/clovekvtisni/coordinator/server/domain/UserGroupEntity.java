package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import cz.clovekvtisni.coordinator.domain.UserGroup;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

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

    @Index
    @NotNull
    private Long eventId;

    @Index
    @NotEmpty
    private String organizationId;

    @Index
    @NotEmpty
    private String name;

    private String roleId;

    private Long createdBy;

    public UserGroupEntity() {
    }

    @Override
    protected UserGroup createTargetEntity() {
        return new UserGroup();
    }

    @Override
    @JsonIgnore
    public Key<UserGroupEntity> getKey() {
        return Key.create(UserGroupEntity.class, id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        return "UserGroupEntity{" +
                "id=" + id +
                ", eventId='" + eventId + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", name='" + name + '\'' +
                ", roleId='" + roleId + '\'' +
                ", createdBy=" + createdBy +
                '}';
    }
}
