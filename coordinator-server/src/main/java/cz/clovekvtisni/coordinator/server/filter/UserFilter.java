package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.NoDeletedFilter;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:22 PM
 */
public class UserFilter extends NoDeletedFilter<UserEntity> {

    private String emailVal;

    private Operator emailOp;

    @Override
    public Class<UserEntity> getEntityClass() {
        return UserEntity.class;
    }

    public String getEmailVal() {
        return emailVal;
    }

    public void setEmailVal(String email) {
        this.emailVal = email;
    }

    public Operator getEmailOp() {
        return emailOp;
    }

    public void setEmailOp(Operator emailOp) {
        this.emailOp = emailOp;
    }

    @Override
    public String toString() {
        return "UserFilter{" +
                "emailVal='" + emailVal + '\'' +
                ", emailOp=" + emailOp +
                '}';
    }
}
