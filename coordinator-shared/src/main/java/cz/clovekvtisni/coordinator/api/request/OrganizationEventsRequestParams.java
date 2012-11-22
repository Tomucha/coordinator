package cz.clovekvtisni.coordinator.api.request;

public class OrganizationEventsRequestParams implements RequestParams {

    /**
     * If null, api call returns event from user's organization.
     */
    private String organizationId;

    public OrganizationEventsRequestParams() {
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String getSignature() {
        return organizationId;
    }
}
