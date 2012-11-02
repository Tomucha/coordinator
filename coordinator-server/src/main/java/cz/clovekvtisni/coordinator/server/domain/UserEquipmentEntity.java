package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import cz.clovekvtisni.coordinator.domain.UserEquipment;

import javax.persistence.Id;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 2.11.12
 */
public class UserEquipmentEntity implements CoordinatorEntity<UserEquipmentEntity> {

    @Id
    private Long id;

    private Long userId;

    private String equipmentId;

    private Long verifiedById;

    private Date verifiedDate;

    private Date verifiedTillDate;

    @Override
    public Key<UserEquipmentEntity> getKey() {
        return Key.create(UserEquipmentEntity.class, id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Long getVerifiedById() {
        return verifiedById;
    }

    public void setVerifiedById(Long verifiedById) {
        this.verifiedById = verifiedById;
    }

    public Date getVerifiedDate() {
        return verifiedDate;
    }

    public void setVerifiedDate(Date verifiedDate) {
        this.verifiedDate = verifiedDate;
    }

    public Date getVerifiedTillDate() {
        return verifiedTillDate;
    }

    public void setVerifiedTillDate(Date verifiedTillDate) {
        this.verifiedTillDate = verifiedTillDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEquipmentEntity that = (UserEquipmentEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
