package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.EventFilter;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
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

    public static final long FLAG_FETCH_LOCATIONS = 1l;

    @FilterResult("#helper.canRead(#entity)")
    EventEntity findById(Long id, long flags);

    @FilterResult("#helper.canRead(#entity)")
    List<EventEntity> findByIds(long flags, Long... ids);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<EventEntity> findByFilter(EventFilter filter, int limit, String bookmark, long flags);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<EventEntity> findByOrganizationFilter(OrganizationInEventFilter filter, int limit, String bookmark, long flags);

    @CheckPermission("#helper.canCreate(#p0)")
    EventEntity createEvent(EventEntity entity);

    @CheckPermission("#helper.canUpdate(#p0)")
    EventEntity updateEvent(EventEntity entity);

    @CheckPermission("#helper.canDelete(#p0)")
    void deleteEvent(EventEntity entity);
}
