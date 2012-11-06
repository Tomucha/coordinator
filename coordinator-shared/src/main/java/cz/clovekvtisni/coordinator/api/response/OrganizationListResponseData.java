package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.domain.config.Organization;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
public class OrganizationListResponseData implements ApiResponseData {

    private List<Organization> organizations;

    public OrganizationListResponseData(List<Organization> organizations) {
        this.organizations = organizations;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }
}
