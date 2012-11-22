package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.domain.config.Organization;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
public class OrganizationEventsResponseData implements ApiResponseData {

    private List<OrganizationInEvent> organizationInEvents;

    public OrganizationEventsResponseData(List<OrganizationInEvent> organizationInEvents) {
        this.organizationInEvents = organizationInEvents;
    }

    public List<OrganizationInEvent> getOrganizationInEvents() {
        return organizationInEvents;
    }

    public void setOrganizationInEvents(List<OrganizationInEvent> organizationInEvents) {
        this.organizationInEvents = organizationInEvents;
    }
}
