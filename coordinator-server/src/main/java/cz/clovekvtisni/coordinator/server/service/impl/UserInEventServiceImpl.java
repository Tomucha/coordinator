package cz.clovekvtisni.coordinator.server.service.impl;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.BoundingBox;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import cz.clovekvtisni.coordinator.domain.RegistrationStatus;
import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserGroupEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserGroupService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Service("userInEventService")
public class UserInEventServiceImpl extends AbstractEntityServiceImpl implements UserInEventService {

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private PoiService poiService;

    @Override
    public ResultList<UserInEventEntity> findByFilter(UserInEventFilter filter, int limit, String bookmark, long flags) {
        ResultList<UserInEventEntity> result = ofy().findByFilter(filter, bookmark, limit);

        populate(result.getResult(), flags);

        return result;
    }

    private void populate(Collection<UserInEventEntity> result, long flags) {
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

        if ((flags & FLAG_FETCH_GROUPS) != 0) {
            for (UserInEventEntity inUser : result) {
                if (inUser.getGroupIdList() != null) {
                    inUser.setGroupEntities(userGroupService.findByIds(0l, inUser.getGroupIdList()));
                }
            }
        }

        if ((flags & FLAG_FETCH_LAST_POI) != 0) {
            for (UserInEventEntity inUser : result) {
                if (inUser.getLastPoiId() != null) {
                    inUser.setLastPoiEntity(poiService.findById(inUser.getLastPoiId(), 0));
                }
            }

        }


    }

    @Override
    public UserInEventEntity create(final UserInEventEntity inEvent) {
        logger.debug("creating " + inEvent);
        return ofy().transact(new Work<UserInEventEntity>() {
            @Override
            public UserInEventEntity run() {
                // FIXME: pripadne vytvorit uzivatele
                if  (inEvent.getStatus() == null)
                    inEvent.setStatus(RegistrationStatus.CONFIRMED);
                updateSystemFields(inEvent, null);

                ofy().put(inEvent);

                return inEvent;
            }
        });
    }

    @Override
    public UserInEventEntity update(final UserInEventEntity inEvent) {
        logger.debug("updating " + inEvent);
        final UserInEventEntity old = findById(inEvent.getEventId(), inEvent.getUserId(), 0l);
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

    @Override
    public UserInEventEntity changeStatus(UserInEventEntity inEvent, RegistrationStatus status) {
        inEvent.setStatus(status);
        return update(inEvent);
    }

    @Override
    public List<UserInEventEntity> findByFilterAndBox(UserInEventFilter filter, double latN, double lonE, double latS, double lonW, long flags) {
        BoundingBox bb = new BoundingBox(latN, lonE, latS, lonW);

        // Calculate the geocells list to be used in the queries (optimize list of cells that complete the given bounding box)
        List<String> cells = GeocellManager.bestBboxSearchCells(bb, null);

        filter.setLastLocationGeoCellsVal(cells);
        filter.setLastLocationGeoCellsOp(Filter.Operator.IN);

        ResultList<UserInEventEntity> result = ofy().findByFilter(filter, null, 0);
        populate(result.getResult(), flags);

        return result.getResult();
    }

    @Override
    public UserInEventEntity findById(long eventId, long userId, long flags) {
        UserInEventEntity entity = ofy().get(UserInEventEntity.createKey(userId, eventId));
        if (entity != null)
            populate(Arrays.asList(entity), flags);
        return entity;
    }

    @Override
    public List<UserInEventEntity> findByIds(long eventId, Set<Long> userIds, long flags) {
        if (userIds == null || userIds.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        Set<Key<UserInEventEntity>> keys = new HashSet<Key<UserInEventEntity>>();
        for (Long id : userIds) {
            if (id != null) {
                keys.add(UserInEventEntity.createKey(id, eventId));
            }
        }

        Map<Key<UserInEventEntity>, UserInEventEntity> entityMap = ofy().get(keys);
        populate(entityMap.values(), flags);

        return new ArrayList<UserInEventEntity>(entityMap.values());

    }

    @Override
    public ResultList<UserInEventEntity> findByUserGroupId(long eventId, final long userGroupId, int limit, String bookmark, long flags) {
        UserGroupEntity userGroup = userGroupService.findById(userGroupId, 0l);
        if (userGroup == null || userGroup.getEventId() != eventId)
            return new ResultList<UserInEventEntity>(new ArrayList<UserInEventEntity>(0), bookmark);

        UserInEventFilter filter = new UserInEventFilter();
        filter.setEventIdVal(userGroup.getEventId());
        filter.addAfterLoadCallback(new Filter.AfterLoadCallback<UserInEventEntity>() {
            @Override
            public boolean accept(UserInEventEntity entity) {
                return entity.getGroupIdList() != null &&
                        Arrays.asList(entity.getGroupIdList()).contains(userGroupId);
            }
        });

        return findByFilter(filter, limit, bookmark, flags);
    }
}
