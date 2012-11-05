package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.server.filter.OrganizationFilter;
import cz.clovekvtisni.coordinator.server.security.FilterResult;

public interface OrganizationService extends Service {

    @FilterResult("#helper.canRead(#entity)")
    Organization findById(String id);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<Organization> findByFilter(OrganizationFilter filter);
}
