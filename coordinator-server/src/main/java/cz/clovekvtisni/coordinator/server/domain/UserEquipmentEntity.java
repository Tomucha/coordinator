package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;
import cz.clovekvtisni.coordinator.domain.UserEquipment;

import javax.persistence.Id;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 2.11.12
 */
@Unindexed
@Cached
@Entity(name = "UserEquipment")
public class UserEquipmentEntity extends AbstractPersistentEntity<UserEquipment, UserEquipmentEntity> implements CoordinatorEntity<UserEquipmentEntity> {

    @Id
    private Long id;

    private Long userId;

    private String equipmentId;

    private Long verifiedById;

    private Date verifiedDate;

    private Date verifiedTillDate;

    public UserEquipmentEntity() {
    }

    @Override
    protected UserEquipment createTargetEntity() {
        return new UserEquipment();
    }

    @Override
    public Key<UserEquipmentEntity> getKey() {
        return Key.create(UserEquipmentEntity.class, id);
    }

    @Override
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
    public String toString() {
        return "UserEquipmentEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", equipmentId='" + equipmentId + '\'' +
                ", verifiedById=" + verifiedById +
                ", verifiedDate=" + verifiedDate +
                ", verifiedTillDate=" + verifiedTillDate +
                '}';
    }
}
