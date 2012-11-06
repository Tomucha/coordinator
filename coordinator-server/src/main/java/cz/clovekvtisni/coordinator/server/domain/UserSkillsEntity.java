package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;
import cz.clovekvtisni.coordinator.domain.UserSkill;

import javax.persistence.Id;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 2.11.12
 */
@Unindexed
@Cached
@Entity(name = "UserSkill")
public class UserSkillsEntity extends AbstractPersistentEntity<UserSkill, UserSkillsEntity> implements CoordinatorEntity<UserSkillsEntity> {

    @Id
    private Long id;

    @Indexed
    private Long userId;

    private String skillId;

    private boolean verified;

    private Long verifiedBy;

    private Date dateVerified;

    private Date validTill;

    public UserSkillsEntity() {
    }

    @Override
    protected UserSkill createTargetEntity() {
        return new UserSkill();
    }

    @Override
    public Key<UserSkillsEntity> getKey() {
        return Key.create(UserSkillsEntity.class, id);
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
        return "UserSkillsEntity{" +
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
