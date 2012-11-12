package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.EventLocationEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.EventFilter;
import cz.clovekvtisni.coordinator.server.filter.EventLocationFilter;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
import cz.clovekvtisni.coordinator.server.filter.result.NoDeletedFilter;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.MaObjectify;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
@Service("eventService")
public class EventServiceImpl extends AbstractServiceImpl implements EventService {
    @Override
    public EventEntity findByEventId(String id, long flags) {
        MaObjectify ofy = noTransactionalObjectify();
        Query<EventEntity> query = ofy.query(EventEntity.class);
        query.filter("eventId =", id);
        EventEntity event = query.get();

        populateEvent(ofy, event, flags);

        return  event;
    }

    @Override
    public ResultList<EventEntity> findByFilter(EventFilter filter, int limit, String bookmark, long flags) {
        MaObjectify ofy = noTransactionalObjectify();

        return ofy.getResult(EventEntity.class, filter, bookmark, limit, new NoDeletedFilter());
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
        return transactionWithResult("creating " + entity, new TransactionWithResultCallback<EventEntity>() {
            @Override
            public EventEntity runInTransaction(Objectify ofy) {
                entity.setId(null);
                updateSystemFields(entity);
                ofy.put(entity);

                if (entity.getEventLocationList() != null) {
                    for (EventLocationEntity location : entity.getEventLocationList()) {
                        location.setId(null);
                        location.setEventId(entity.getEventId());
                        updateSystemFields(location);
                        Key<EventLocationEntity> inserted = ofy.put(location);
                        location.setId(inserted.getId());
                    }
                }

                return entity;
            }
        });
    }

    @Override
    public EventEntity updateEvent(final EventEntity entity) {
        return transactionWithResult("updating " + entity, new TransactionWithResultCallback<EventEntity>() {
            @Override
            public EventEntity runInTransaction(Objectify ofy) {
                updateSystemFields(entity);
                ofy.put(entity);

                List<EventLocationEntity> locationList = entity.getEventLocationList();
                if (locationList != null) {
                    Map<Long, EventLocationEntity> savedLocationMap = new HashMap<Long, EventLocationEntity>();
                    for (EventLocationEntity saved : getEventLocations((MaObjectify) ofy, entity.getEventId())) {
                        savedLocationMap.put(saved.getId(), saved);
                    }

                    List<EventLocationEntity> newList = new ArrayList<EventLocationEntity>(locationList.size());
                    for (EventLocationEntity location : locationList) {
                        if (location.isNew()) {
                            location.setId(null);
                            location.setEventId(entity.getEventId());
                            updateSystemFields(location);
                            ofy.put(location);
                            newList.add(location);

                        } else if (location.isDeleted()) {
                            ofy.delete(location);
                            savedLocationMap.remove(location.getId());

                        } else {
                            updateSystemFields(location);
                            ofy.put(location);
                            savedLocationMap.remove(location.getId());
                            newList.add(location);
                        }
                    }

                    for (EventLocationEntity toDelete : savedLocationMap.values()) {
                        ofy.delete(toDelete);
                    }

                    entity.setEventLocationList(newList);
                }

                return entity;
            }
        });
    }

    @Override
    public void deleteEvent(EventEntity entity) {
        // TODO
    }

    private List<EventLocationEntity> getEventLocations(MaObjectify ofy, String eventId) {
        EventLocationFilter filter = new EventLocationFilter();
        filter.setEventIdVal(eventId);

        ResultList<EventLocationEntity> locations = ofy.getResult(EventLocationEntity.class, filter, null, 0, new NoDeletedFilter<EventLocationEntity>());

        return locations.getResult();
    }

    @Override
    public ResultList<OrganizationInEventEntity> getOrganizationInEventList(String organizationId, int limit, String bookmark, long flags) {
        MaObjectify ofy = noTransactionalObjectify();

        OrganizationInEventFilter filter = new OrganizationInEventFilter();
        filter.setOrganizationIdVal(organizationId);

        ResultList<OrganizationInEventEntity> result = ofy.getResult(OrganizationInEventEntity.class, filter, bookmark, limit, new NoDeletedFilter());

        if ((flags & EventService.FLAG_FETCH_EVENT) != 0) {
            Map<Key<EventEntity>, OrganizationInEventEntity> inEventMap = new HashMap<Key<EventEntity>, OrganizationInEventEntity>(result.getResult().size());
            for (OrganizationInEventEntity inEvent : result.getResult()) {
                Key<EventEntity> key = Key.create(EventEntity.class, inEvent.getId());
                inEventMap.put(key, inEvent);
            }
            Map<Key<EventEntity>, EventEntity> entityMap = ofy.get(inEventMap.keySet());
            for (Map.Entry<Key<EventEntity>, EventEntity> entry : entityMap.entrySet()) {
                if (entry.getValue().isDeleted()) continue;
                populateEvent(ofy, entry.getValue(), flags);
                inEventMap.get(entry.getKey()).setEventEntity(entry.getValue());
            }
        }
        
        return result;
    }

    private void populateEvent(MaObjectify ofy, EventEntity entity, long flags) {
        if ((flags & EventService.FLAG_FETCH_LOCATIONS) != 0) {
            EventLocationFilter filter = new EventLocationFilter();
            filter.setEventIdVal(entity.getEventId());
            ResultList<EventLocationEntity> result = ofy.getResult(EventLocationEntity.class, filter, null, 0, new NoDeletedFilter());
            entity.setEventLocationList(result.getResult());
        }
    }
}
