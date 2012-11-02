package cz.clovekvtisni.coordinator.domain;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 2.11.12
 */
public class UserEquipment extends IdentifiableEntity {

    private Long userId;

    private String equipmentId;

    private Long verifiedById;

    private Date verifiedDate;

    private Date verifiedTillDate;

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
}
