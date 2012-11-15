package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.server.domain.UserSkillEntity;
import cz.clovekvtisni.coordinator.server.tool.objectify.NoDeletedFilter;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 14.11.12
 */
public class UserSkillFilter extends NoDeletedFilter<UserSkillEntity> {

    private Long userIdVal;

    private Operator userIdOp = Operator.EQ;

    public Long getUserIdVal() {
        return userIdVal;
    }

    public void setUserIdVal(Long userIdVal) {
        this.userIdVal = userIdVal;
    }

    public Operator getUserIdOp() {
        return userIdOp;
    }

    public void setUserIdOp(Operator userIdOp) {
        this.userIdOp = userIdOp;
    }

    @Override
    public Class<UserSkillEntity> getEntityClass() {
        return UserSkillEntity.class;
    }
}
