package cz.clovekvtisni.coordinator.api.request;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 23.11.12
 */
public class UserFilterRequestParams implements RequestParams {

    private Date modifiedFrom;

    public UserFilterRequestParams() {
    }

    public Date getModifiedFrom() {
        return modifiedFrom;
    }

    public void setModifiedFrom(Date modifiedFrom) {
        this.modifiedFrom = modifiedFrom;
    }

    @Override
    public String getSignature() {
        return "" + modifiedFrom;
    }
}
