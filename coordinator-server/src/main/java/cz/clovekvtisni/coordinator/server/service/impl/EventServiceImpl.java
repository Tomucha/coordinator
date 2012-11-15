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
import cz.clovekvtisni.coordinator.server.tool.objectify.MaObjectify;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
@Service("eventService")
public class EventServiceImpl extends AbstractEntityServiceImpl implements EventService {
    @Override
    public EventEntity findByEventId(String id, long flags) {
        EventFilter filter = new EventFilter();
        filter.setEventIdVal(id);
        ResultList<EventEntity> result = ofy().findByFilter(filter, null, 1);
        if (result.getResultSize() == 0) return null;
        EventEntity event = result.firstResult();
        populateEvent(ofy(), event, flags);

        return  event;
    }

    @Override
    public ResultList<EventEntity> findByFilter(EventFilter filter, int limit, String bookmark, long flags) {
        if (filter.getOrganizationIdVal() != null) {
            return findByOrganization(filter.getOrganizationIdVal(), limit, bookmark, flags);
        }
        return ofy().findByFilter(filter, bookmark, limit);
    }

    @Override
    public ResultList<EventEntity> findByOrganization(String organizationId, int limit, String bookmark, long flags) {
        ResultList<OrganizationInEventEntity> inEventList = getOrganizationInEventList(organizationId, limit, bookmark, flags | FLAG_FETCH_EVENT);
        List<EventEntity> events = new ArrayList<EventEntity>(inEventList.getResultSize());
        for (OrganizationInEventEntity inEventEntity : inEventList) {
            if (inEventEntity.getEventEntity() != null) {
                events.add(inEventEntity.getEventEntity());
            }
        }

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

    protected void saveFields(EventEntity entity, EventEntity oldEntity) {
        if (entity.getEventLocationList() == null) return;
        entity.setEventLocationList(mergeEntities(oldEntity != null ? oldEntity.getEventLocationList() : null, entity.getEventLocationList()).toArray(new EventLocationEntity[0]));
        for (EventLocationEntity location : entity.getEventLocationList()) {
            if (oldEntity == null)
                location.setId(null);
            location.setEventId(entity.getEventId());
            location.setParentKey(entity.getKey());
            updateSystemFields(location, null);
            if (location.isDeleted())
                ofy().delete(location);
            else
                ofy().put(location);
        }
    }

    @Override
    public EventEntity updateEvent(final EventEntity entity) {
        logger.debug("updating " + entity);
        final EventEntity old = findByEventId(entity.getEventId(), EventService.FLAG_FETCH_LOCATIONS);
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

    @Override
    public ResultList<OrganizationInEventEntity> getOrganizationInEventList(String organizationId, int limit, String bookmark, long flags) {
        OrganizationInEventFilter filter = new OrganizationInEventFilter();
        filter.setOrganizationIdVal(organizationId);

        ResultList<OrganizationInEventEntity> result = ofy().findByFilter(filter, bookmark, limit);

        if ((flags & FLAG_FETCH_EVENT) != 0) {
            Map<Key<EventEntity>, OrganizationInEventEntity> inEventMap = new HashMap<Key<EventEntity>, OrganizationInEventEntity>(result.getResult().size());
            for (OrganizationInEventEntity inEvent : result.getResult()) {
                Key<EventEntity> key = Key.create(EventEntity.class, inEvent.getId());
                inEventMap.put(key, inEvent);
            }
            Map<Key<EventEntity>, EventEntity> entityMap = ofy().get(inEventMap.keySet());
            for (Map.Entry<Key<EventEntity>, EventEntity> entry : entityMap.entrySet()) {
                if (entry.getValue().isDeleted()) continue;
                populateEvent(ofy(), entry.getValue(), flags);
                inEventMap.get(entry.getKey()).setEventEntity(entry.getValue());
            }
        }
        
        return result;
    }

    private void populateEvent(MaObjectify ofy, EventEntity entity, long flags) {
        if ((flags & EventService.FLAG_FETCH_LOCATIONS) != 0) {
            EventLocationFilter filter = new EventLocationFilter();
            filter.setEventIdVal(entity.getEventId());
            ResultList<EventLocationEntity> result = ofy.findByFilter(filter, null, 0);
            entity.setEventLocationList(result.getResult().toArray(new EventLocationEntity[0]));
        }
    }
}
