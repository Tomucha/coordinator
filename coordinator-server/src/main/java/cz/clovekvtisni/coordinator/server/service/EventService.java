package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.filter.EventFilter;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.security.FilterResult;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
public interface EventService extends Service {

    @FilterResult("#helper.canRead(#entity)")
    EventEntity findByEventId(String id);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<EventEntity> findByFilter(EventFilter filter, int limit, String bookmark);

    @CheckPermission("#helper.canCreate(#entity)")
    EventEntity createEvent(EventEntity event);

    @CheckPermission("#helper.canDelete(#entity)")
    void deleteEvent(EventEntity event);
}
