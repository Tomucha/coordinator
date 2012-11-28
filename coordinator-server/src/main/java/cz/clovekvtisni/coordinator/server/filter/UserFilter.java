package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.NoDeletedFilter;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:22 PM
 */
public class UserFilter extends NoDeletedFilter<UserEntity> {

    private String emailVal;

    private Operator emailOp;

    private String organizationIdVal;

    private Operator organizationIdOp = Operator.EQ;

    private Date modifiedDateVal;

    private Operator modifiedDateOp = Operator.EQ;

    public UserFilter() {
    }

    public UserFilter(boolean includeDeleted) {
        super(includeDeleted);
    }

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

    public String getOrganizationIdVal() {
        return organizationIdVal;
    }

    public void setOrganizationIdVal(String organizationIdVal) {
        this.organizationIdVal = organizationIdVal;
    }

    public Operator getOrganizationIdOp() {
        return organizationIdOp;
    }

    public void setOrganizationIdOp(Operator organizationIdOp) {
        this.organizationIdOp = organizationIdOp;
    }

    public Date getModifiedDateVal() {
        return modifiedDateVal;
    }

    public void setModifiedDateVal(Date modifiedDateVal) {
        this.modifiedDateVal = modifiedDateVal;
    }

    public Operator getModifiedDateOp() {
        return modifiedDateOp;
    }

    public void setModifiedDateOp(Operator modifiedDateOp) {
        this.modifiedDateOp = modifiedDateOp;
    }

    @Override
    public String toString() {
        return "UserFilter{" +
                "emailVal='" + emailVal + '\'' +
                ", emailOp=" + emailOp +
                ", organizationIdVal='" + organizationIdVal + '\'' +
                ", organizationIdOp=" + organizationIdOp +
                ", modifiedDateVal=" + modifiedDateVal +
                ", modifiedDateOp=" + modifiedDateOp +
                '}';
    }
}
