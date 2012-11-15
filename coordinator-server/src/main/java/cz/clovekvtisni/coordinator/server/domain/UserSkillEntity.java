package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.*;
import cz.clovekvtisni.coordinator.domain.UserSkill;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 2.11.12
 */
@Cache
@Entity(name = "UserSkill")
public class UserSkillEntity extends AbstractPersistentEntity<UserSkill, UserSkillEntity> implements CoordinatorEntity<UserSkillEntity> {

    @Id
    private Long id;

    @Parent
    private Key<UserEntity> parentKey;

    @Index
    private Long userId;

    private String skillId;

    private boolean verified;

    private Long verifiedBy;

    private Date dateVerified;

    private Date validTill;

    public UserSkillEntity() {
    }

    @Override
    protected UserSkill createTargetEntity() {
        return new UserSkill();
    }

    @Override
    public Key<UserSkillEntity> getKey() {
        return Key.create(UserSkillEntity.class, id);
    }

    public Key<UserEntity> getParentKey() {
        return parentKey;
    }

    public void setParentKey(Key<UserEntity> parentKey) {
        this.parentKey = parentKey;
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

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Long getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(Long verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public Date getDateVerified() {
        return dateVerified;
    }

    public void setDateVerified(Date dateVerified) {
        this.dateVerified = dateVerified;
    }

    public Date getValidTill() {
        return validTill;
    }

    public void setValidTill(Date validTill) {
        this.validTill = validTill;
    }

    @Override
    public String toString() {
        return "UserSkillEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", skillId='" + skillId + '\'' +
                ", verified=" + verified +
                ", verifiedBy=" + verifiedBy +
                ", dateVerified=" + dateVerified +
                ", validTill=" + validTill +
                '}';
    }
}
