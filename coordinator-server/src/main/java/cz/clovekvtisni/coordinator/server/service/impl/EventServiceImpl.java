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

import java.util.HashMap;
import java.util.Map;

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
    public ResultList<EventEntity> findByFilter(EventFilter filter, int limit, String bookmark) {
        MaObjectify ofy = noTransactionalObjectify();

        return ofy.getResult(EventEntity.class, filter, bookmark, limit, new NoDeletedFilter());
    }

    @Override
    public EventEntity createEvent(final EventEntity event) {
        return transactionWithResult("creating " + event, new TransactionWithResultCallback<EventEntity>() {
            @Override
            public EventEntity runInTransaction(Objectify ofy) {
                event.setId(null);
                ofy.put(event);

                if (event.getEventLocationList() != null) {
                    for (EventLocationEntity location : event.getEventLocationList()) {
                        location.setId(null);
                        location.setEventId(event.getEventId());
                        Key<EventLocationEntity> inserted = ofy.put(location);
                        location.setId(inserted.getId());
                    }
                }

                return event;
            }
        });
    }

    @Override
    public EventEntity updateEvent(EventEntity event) {
        return null;  // TODO
    }

    @Override
    public void deleteEvent(EventEntity event) {
        // TODO
    }

    @Override
    public ResultList<OrganizationInEventEntity> getOrganizatioInEventList(String organizationId, long flags) {
        MaObjectify ofy = noTransactionalObjectify();

        OrganizationInEventFilter filter = new OrganizationInEventFilter();
        filter.setOrganizationIdVal(organizationId);

        ResultList<OrganizationInEventEntity> result = ofy.getResult(OrganizationInEventEntity.class, filter, null, 0, new NoDeletedFilter());

        if ((flags & EventService.FLAG_FETCH_EVENT) != 0) {
            Map<Key<EventEntity>, OrganizationInEventEntity> inEventMap = new HashMap<Key<EventEntity>, OrganizationInEventEntity>(result.getResult().size());
            for (OrganizationInEventEntity inEvent : result.getResult()) {
                Key<EventEntity> key = Key.create(EventEntity.class, inEvent.getId());
                inEventMap.put(key, inEvent);
            }
            Map<Key<EventEntity>, EventEntity> entityMap = ofy.get(inEventMap.keySet());
            for (Map.Entry<Key<EventEntity>, EventEntity> entry : entityMap.entrySet()) {
                populateEvent(ofy, entry.getValue(), flags);
                inEventMap.get(entry.getKey()).setEventEntity(entry.getValue());
            }
        }
        
        return result;
    }

    private void populateEvent(MaObjectify ofy, EventEntity event, long flags) {
        if ((flags & EventService.FLAG_FETCH_LOCATIONS) != 0) {
            EventLocationFilter filter = new EventLocationFilter();
            filter.setEventIdVal(event.getEventId());
            ResultList<EventLocationEntity> result = ofy.getResult(EventLocationEntity.class, filter, null, 0, new NoDeletedFilter());
            event.setEventLocationList(result.getResult());
        }
    }
}
