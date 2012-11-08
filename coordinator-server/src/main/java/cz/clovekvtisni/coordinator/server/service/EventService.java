package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.EventFilter;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.security.FilterResult;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
public interface EventService extends Service {

    public static final long FLAG_FETCH_EVENT = 1l;

    public static final long FLAG_FETCH_LOCATIONS = 2l;

    @FilterResult("#helper.canRead(#entity)")
    EventEntity findByEventId(String id, long flags);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<EventEntity> findByFilter(EventFilter filter, int limit, String bookmark, long flags);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<EventEntity> findByOrganization(String organizationId, int limit, String bookmark, long flags);

    @CheckPermission("#helper.canCreate(#entity)")
    EventEntity createEvent(EventEntity event);

    @CheckPermission("#helper.canUpdate(#entity)")
    EventEntity updateEvent(EventEntity event);

    @CheckPermission("#helper.canDelete(#entity)")
    void deleteEvent(EventEntity event);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<OrganizationInEventEntity> getOrganizationInEventList(String organizationId, int limit, String bookmark, long flags);
}
