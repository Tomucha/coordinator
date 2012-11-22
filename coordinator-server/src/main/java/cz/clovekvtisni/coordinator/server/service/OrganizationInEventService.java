package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.security.FilterResult;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;

public interface OrganizationInEventService extends Service {

    public static final long FLAG_FETCH_EVENT = 1l;

    @FilterResult("#helper.canRead(#entity)")
    OrganizationInEventEntity findById(Long id, long flags);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<OrganizationInEventEntity> findByFilter(OrganizationInEventFilter filter, int limit, String bookmark, long flags);

    @CheckPermission("#helper.canCreate(#entity)")
    OrganizationInEventEntity create(OrganizationInEventEntity inEvent);

    @CheckPermission("#helper.canUpdate(#entity)")
    OrganizationInEventEntity update(OrganizationInEventEntity inEvent);
}
