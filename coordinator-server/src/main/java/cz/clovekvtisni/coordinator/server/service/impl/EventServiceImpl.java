package cz.clovekvtisni.coordinator.server.service.impl;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.filter.EventFilter;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.service.ResultList;
import cz.clovekvtisni.coordinator.util.ValueTool;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
@Service("eventService")
public class EventServiceImpl extends AbstractServiceImpl implements EventService {
    @Override
    public EventEntity findByEventId(String id) {
        Query<EventEntity> query = noTransactionalObjectify().query(EventEntity.class);
        query.filter("eventId =", id);
        EventEntity entity = query.get();

        return  entity;
    }

    @Override
    public ResultList<EventEntity> findByFilter(EventFilter filter, int limit, String bookmark) {
        Query<EventEntity> query = noTransactionalObjectify().query(EventEntity.class);

        // TODO filter

        if (!ValueTool.isEmpty(bookmark)) {
            query.startCursor(Cursor.fromWebSafeString(bookmark));
        }

        //TODO: ordering (support in AbstractFilter)
        query.order("id");

        QueryResultIterator<EventEntity> iterator = query.iterator();
        List<EventEntity> events = new ArrayList<EventEntity>();
        while (iterator.hasNext()) {
            EventEntity event = iterator.next();
            events.add(event);
            if (--limit <= 0) {
                return new ResultList<EventEntity>(events, iterator.getCursor().toWebSafeString());
            }
        }

        return new ResultList<EventEntity>(events, null);
    }

    @Override
    public EventEntity createEvent(final EventEntity event) {
        return transactionWithResult("creating " + event, new TransactionWithResultCallback<EventEntity>() {
            @Override
            public EventEntity runInTransaction(Objectify ofy) {
                event.setId(null);
                ofy.put(event);

                return event;
            }
        });
    }

    @Override
    public void deleteEvent(EventEntity event) {
        // TODO
    }
}
