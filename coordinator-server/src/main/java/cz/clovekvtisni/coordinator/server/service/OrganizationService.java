package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.OrganizationFilter;
import cz.clovekvtisni.coordinator.server.security.FilterResult;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;

public interface OrganizationService extends Service {

    @FilterResult("#helper.canRead(#entity)")
    Organization findById(String id, long flags);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<Organization> findByFilter(OrganizationFilter filter);
}
