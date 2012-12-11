package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Service("userInEventService")
public class UserInEventServiceImpl extends AbstractEntityServiceImpl implements UserInEventService {

    @Override
    public UserInEventEntity findById(Long id, Long parentUserId, long flags) {
        UserInEventEntity inEvent = ofy().load().key(Key.create(Key.create(UserEntity.class, parentUserId), UserInEventEntity.class, id)).get();

        populate(Arrays.asList(new UserInEventEntity[] {inEvent}), flags);

        return inEvent;
    }

    @Override
    public ResultList<UserInEventEntity> findByFilter(UserInEventFilter filter, int limit, String bookmark, long flags) {
        ResultList<UserInEventEntity> result = ofy().findByFilter(filter, bookmark, limit);

        populate(result.getResult(), flags);

        return result;
    }

    private void populate(List<UserInEventEntity> result, long flags) {
        if ((flags & FLAG_FETCH_EVENT) != 0) {
            Map<Key<EventEntity>, UserInEventEntity> inEventMap = new HashMap<Key<EventEntity>, UserInEventEntity>(result.size());
            for (UserInEventEntity inEvent : result) {
                Key<EventEntity> key = Key.create(EventEntity.class, inEvent.getEventId());
                inEventMap.put(key, inEvent);
            }
            Map<Key<EventEntity>, EventEntity> entityMap = ofy().get(inEventMap.keySet());
            for (Map.Entry<Key<EventEntity>, EventEntity> entry : entityMap.entrySet()) {
                if (entry.getValue().isDeleted()) continue;
                inEventMap.get(entry.getKey()).setEventEntity(entry.getValue());
            }
        }

        if ((flags & FLAG_FETCH_USER) != 0) {
            Map<Key<UserEntity>, UserInEventEntity> inUserMap = new HashMap<Key<UserEntity>, UserInEventEntity>(result.size());
            for (UserInEventEntity inUser : result) {
                Key<UserEntity> key = Key.create(UserEntity.class, inUser.getUserId());
                inUserMap.put(key, inUser);
            }
            Map<Key<UserEntity>, UserEntity> entityMap = ofy().get(inUserMap.keySet());
            for (Map.Entry<Key<UserEntity>, UserEntity> entry : entityMap.entrySet()) {
                if (entry.getValue().isDeleted()) continue;
                inUserMap.get(entry.getKey()).setUserEntity(entry.getValue());
            }
        }
    }

    @Override
    public UserInEventEntity create(final UserInEventEntity inEvent) {
        logger.debug("creating " + inEvent);
        return ofy().transact(new Work<UserInEventEntity>() {
            @Override
            public UserInEventEntity run() {
                inEvent.setId(null);
                updateSystemFields(inEvent, null);

                ofy().put(inEvent);

                return inEvent;
            }
        });
    }

    @Override
    public UserInEventEntity update(final UserInEventEntity inEvent) {
        logger.debug("updating " + inEvent);
        final UserInEventEntity old = findById(inEvent.getId(), inEvent.getUserId(), 0l);
        if (old == null)
            throw NotFoundException.idNotExist(UserInEvent.class.getSimpleName(), inEvent.getId());
        return ofy().transact(new Work<UserInEventEntity>() {
            @Override
            public UserInEventEntity run() {
                // we cannot change this fields
                inEvent.setEventId(old.getEventId());
                inEvent.setUserId(old.getUserId());

                updateSystemFields(inEvent, old);

                ofy().put(inEvent);

                return inEvent;
            }
        });
    }
}
