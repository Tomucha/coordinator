package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.*;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.UserEquipment;
import cz.clovekvtisni.coordinator.domain.UserSkill;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:07 PM
 */
@Cache
@Entity(name = "User")
public class UserEntity extends AbstractPersistentEntity<User, UserEntity> {

    @Id
    private Long id;

    private String[] authKey;

    private String firstName;

    private String lastName;

    @Index
    @NotEmpty
    @Email
    private String email;

    private String password;

    private String phone;

    @Index
    private String organizationId;

    private Date birthday;

    private String addressLine;

    private String city;

    private String zip;

    private String country;

    private Date dateSuspended;

    private String reasonSuspended;

    @NotEmpty
    private String[] roleIdList;

    @Ignore
    private UserEquipmentEntity[] equipmentEntityList;

    @Ignore
    private UserSkillEntity[] skillEntityList;

    public UserEntity() {
    }

    @Override
    protected User createTargetEntity() {
        return new User();
    }

    @Override
    public Key<UserEntity> getKey() {
        return Key.create(UserEntity.class, id);
    }

    @Override
    public User buildTargetEntity() {
        User user = super.buildTargetEntity();
        if (equipmentEntityList != null) {
            List<UserEquipment> equipmentList = new ArrayList<UserEquipment>(equipmentEntityList.length);
            for (UserEquipmentEntity equipmentEntity : equipmentEntityList) {
                equipmentList.add(equipmentEntity.buildTargetEntity());
            }
            user.setEquipmentList(equipmentList.toArray(new UserEquipment[0]));
        }

        if (skillEntityList != null) {
            List<UserSkill> skillList = new ArrayList<UserSkill>(skillEntityList.length);
            for (UserSkillEntity skillEntity : skillEntityList) {
                skillList.add(skillEntity.buildTargetEntity());
            }
            user.setSkillList(skillList.toArray(new UserSkill[0]));
        }

        return user;
    }

    @Override
    public UserEntity populateFrom(UserEntity entity) {
        return super.populateFrom(entity);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String[] getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String[] authKey) {
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

    public String[] getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(String[] roleIdList) {
        this.roleIdList = roleIdList;
    }

    public UserEquipmentEntity[] getEquipmentEntityList() {
        return equipmentEntityList;
    }

    public void setEquipmentEntityList(UserEquipmentEntity[] equipmentEntityList) {
        this.equipmentEntityList = equipmentEntityList;
    }

    public UserSkillEntity[] getSkillEntityList() {
        return skillEntityList;
    }

    public void setSkillEntityList(UserSkillEntity[] skillEntityList) {
        this.skillEntityList = skillEntityList;
    }
    
    public Map<String, UserEquipmentEntity> getEquipmentEntityMap() {
        if (equipmentEntityList == null) return new HashMap<String, UserEquipmentEntity>(); 
        Map<String, UserEquipmentEntity> map = new HashMap<String, UserEquipmentEntity>(equipmentEntityList.length);
        for (UserEquipmentEntity equipmentEntity : equipmentEntityList) {
            map.put(equipmentEntity.getEquipmentId(), equipmentEntity);
        }
        return map;
    }

    public Map<String, UserSkillEntity> getSkillEntityMap() {
        if (skillEntityList == null) return new HashMap<String, UserSkillEntity>();
        Map<String, UserSkillEntity> map = new HashMap<String, UserSkillEntity>(skillEntityList.length);
        for (UserSkillEntity skillEntity : skillEntityList) {
            map.put(skillEntity.getSkillId(), skillEntity);
        }
        return map;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", authKey=" + authKey +
                ", email='" + email + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", city='" + city + '\'' +
                ", zip='" + zip + '\'' +
                ", country='" + country + '\'' +
                ", dateSuspended=" + dateSuspended +
                ", reasonSuspended='" + reasonSuspended + '\'' +
                ", roleIdList=" + roleIdList +
                ", addressLine='" + addressLine + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
