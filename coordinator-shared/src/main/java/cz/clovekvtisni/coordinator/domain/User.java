package cz.clovekvtisni.coordinator.domain;

import cz.clovekvtisni.coordinator.util.ValueTool;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/27/12
 * Time: 12:16 AM
 */
public class User extends AbstractModifiableEntity {

    private String[] authKey;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String organizationId;

    private Date birthday;

    private String addressLine;

    private String city;

    private String zip;

    private String country;

    private Date dateSuspended;

    private String reasonSuspended;

    private String[] roleIdList;

    private UserEquipment[] equipmentList;

    private UserSkill[] skillList;

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

    public String[] getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String[] authKey) {
        this.authKey = authKey;
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

    public String[] getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(String[] roleIdList) {
        this.roleIdList = roleIdList;
    }

    public UserEquipment[] getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(UserEquipment[] equipmentList) {
        this.equipmentList = equipmentList;
    }

    public UserSkill[] getSkillList() {
        return skillList;
    }

    public void setSkillList(UserSkill[] skillList) {
        this.skillList = skillList;
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
        return ValueTool.isEmpty(fullName) ? email : fullName;
    }
}
