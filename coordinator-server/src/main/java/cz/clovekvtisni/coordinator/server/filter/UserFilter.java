package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:22 PM
 */
public class UserFilter extends Filter {

    private String emailVal;

    private Operator emailOp;

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
