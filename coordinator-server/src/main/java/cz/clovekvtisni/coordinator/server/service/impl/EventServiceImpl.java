package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.EventLocationEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.EventFilter;
import cz.clovekvtisni.coordinator.server.filter.EventLocationFilter;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.service.OrganizationInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
@Service("eventService")
public class EventServiceImpl extends AbstractEntityServiceImpl implements EventService {

    @Autowired
    private OrganizationInEventService organizationInEventService;

    @Override
    public EventEntity findByEventId(String id, long flags) {
        EventFilter filter = new EventFilter();
        filter.setEventIdVal(id);
        ResultList<EventEntity> result = ofy().findByFilter(filter, null, 1);
        if (result.getResultSize() == 0) return null;
        EventEntity event = result.firstResult();
        populate(Arrays.asList(new EventEntity[] {event}), flags);

        return  event;
    }

    @Override
    public EventEntity findById(Long id, long flags) {
        EventEntity entity = ofy().get(Key.create(EventEntity.class, id));
        populate(Arrays.asList(new EventEntity[]{entity}), flags);
        return entity;
    }

    @Override
    public ResultList<EventEntity> findByFilter(EventFilter filter, int limit, String bookmark, long flags) {
        return ofy().findByFilter(filter, bookmark, limit);
    }

    @Override
    public ResultList<EventEntity> findByOrganizationFilter(OrganizationInEventFilter filter, int limit, String bookmark, long flags) {
        ResultList<OrganizationInEventEntity> inEventList = organizationInEventService.findByFilter(filter, limit, bookmark, OrganizationInEventService.FLAG_FETCH_EVENT);
        List<EventEntity> events = new ArrayList<EventEntity>(inEventList.getResultSize());
        for (OrganizationInEventEntity inEventEntity : inEventList) {
            if (inEventEntity.getEventEntity() != null) {
                EventEntity event = inEventEntity.getEventEntity();
                events.add(event);
            }
        }

        populate(events, flags);

        return new ResultList<EventEntity>(events, bookmark);
    }

    @Override
    public EventEntity createEvent(final EventEntity entity) {
        logger.debug("creating " + entity);
        return ofy().transact(new Work<EventEntity>() {
            @Override
            public EventEntity run() {
                entity.setId(null);
                updateSystemFields(entity, null);
                ofy().put(entity);

                saveFields(entity, null);

                return entity;
            }
        });
    }

    @Override
    public EventEntity updateEvent(final EventEntity entity) {
        logger.debug("updating " + entity);
        final EventEntity old = findByEventId(entity.getEventKey(), EventService.FLAG_FETCH_LOCATIONS);
        return ofy().transact(new Work<EventEntity>() {
            @Override
            public EventEntity run() {
                // TODO co kdyz appengina nic nevrati? vyhodit vyjimku? Nebo nejak poresit v MaObjectify?
                updateSystemFields(entity, old);
                ofy().put(entity);
                saveFields(entity, old);

                return entity;
            }
        });
    }

    protected void saveFields(EventEntity entity, EventEntity oldEntity) {
        if (entity.getEventLocationEntityList() == null) return;
        entity.setEventLocationEntityList(mergeEntities(oldEntity != null ? oldEntity.getEventLocationEntityList() : null, entity.getEventLocationEntityList()).toArray(new EventLocationEntity[0]));
        for (EventLocationEntity location : entity.getEventLocationEntityList()) {
            if (oldEntity == null)
                location.setId(null);
            location.setEventId(entity.getEventKey());
            location.setParentKey(entity.getKey());
            updateSystemFields(location, null);
            if (location.isDeleted())
                ofy().delete(location);
            else
                ofy().put(location);
        }
    }

    @Override
    public void deleteEvent(EventEntity entity) {
        // TODO
    }

    private List<EventLocationEntity> getEventLocations(String eventId) {
        EventLocationFilter filter = new EventLocationFilter();
        filter.setEventIdVal(eventId);

        ResultList<EventLocationEntity> locations = ofy().findByFilter(filter, null, 0);

        return locations.getResult();
    }

    private void populate(Collection<EventEntity> events, long flags) {
        for (EventEntity entity : events) {
            if ((flags & EventService.FLAG_FETCH_LOCATIONS) != 0) {
                EventLocationFilter filter = new EventLocationFilter();
                filter.setEventIdVal(entity.getEventKey());
                ResultList<EventLocationEntity> result = ofy().findByFilter(filter, null, 0);
                entity.setEventLocationEntityList(result.getResult().toArray(new EventLocationEntity[0]));
            }
        }
    }


    @Override
    public List<EventEntity> findByIds(long flags, Long... ids) {
        if (ids == null)
            return null;

        Set<Key<EventEntity>> keys = new HashSet<Key<EventEntity>>(ids.length);
        for (Long id : ids) {
            if (id != null)
                keys.add(Key.create(EventEntity.class, id));
        }

        Map<Key<EventEntity>, EventEntity> entityMap = ofy().get(keys);
        populate(entityMap.values(), flags);

        return new ArrayList<EventEntity>(entityMap.values());
    }

}
