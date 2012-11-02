package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;
import cz.clovekvtisni.coordinator.domain.User;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;

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

    private List<String> authKey;

    private String firstName;

    private String lastName;

    @Indexed
    private String email;

    private String password;

    private String phone;

    private String organizationId;

    private Date birthday;

    private String addressLine;

    private String city;

    private String zip;

    private String country;

    private Date dateSuspended;

    private String reasonSuspended;

    private List<String> roleIdList;

    @Override
    public Key<UserEntity> getKey() {
        return Key.create(UserEntity.class, id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getAuthKey() {
        return authKey;
    }

    public void setAuthKey(List<String> authKey) {
        this.authKey = authKey;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getDateSuspended() {
        return dateSuspended;
    }

    public void setDateSuspended(Date dateSuspended) {
        this.dateSuspended = dateSuspended;
    }

    public String getReasonSuspended() {
        return reasonSuspended;
    }

    public void setReasonSuspended(String reasonSuspended) {
        this.reasonSuspended = reasonSuspended;
    }

    public List<String> getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(List<String> roleIdList) {
        this.roleIdList = roleIdList;
    }

    public UserEntity() {
    }

    public UserEntity(User user) {
        id = user.getId();
        email = user.getEmail();
        if (user.getNewPassword() != null) {
            password = user.getNewPassword(); // TODO hash it!
        }
        // TODO
    }

    public User buildUser() {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAuthKey(authKey);
        user.setAddressLine(addressLine);
        user.setBirthday(birthday);
        user.setCity(city);
        user.setCountry(country);
        user.setDateSuspended(dateSuspended);
        user.setOrganizationId(organizationId);
        user.setPhone(phone);
        user.setReasonSuspended(reasonSuspended);
        user.setRoleIdList(roleIdList);
        user.setZip(zip);
        return user;
    }
}
