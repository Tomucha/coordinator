package cz.clovekvtisni.coordinator.api.request;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 23.11.12
 */
public class EventFilterRequestParams implements RequestParams {

    private String organizationId;

    public EventFilterRequestParams() {
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String getSignature() {
        return "";
    }
}
