package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.util.ValueTool;

import javax.persistence.Id;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:07 PM
 */
@Unindexed
@Cached
@Entity(name = "User")
public class UserEntity implements CoordinatorEntity<UserEntity> {

    @Id
    private Long id;

    @Indexed
    private String login;

    private String password;

    private String firstName;

    private String lastName;

    private String email;

    public UserEntity() {
    }

    public UserEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Key<UserEntity> getKey() {
        return Key.create(UserEntity.class, id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity that = (UserEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "login='" + login + '\'' +
                ", id=" + id +
                '}';
    }

    public User buildUser() {
        User user = new User();
        user.setId(id);
        user.setLogin(login);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (firstName != null) {
            sb.append(firstName);
        }
        if (!ValueTool.isEmpty(lastName)) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(lastName);
        }
        String fullName = sb.toString();
        return ValueTool.isEmpty(fullName) ? login : fullName;
    }
}
