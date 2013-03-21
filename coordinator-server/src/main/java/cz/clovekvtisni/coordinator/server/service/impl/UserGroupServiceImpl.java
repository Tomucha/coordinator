package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import cz.clovekvtisni.coordinator.server.domain.UniqueIndexEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserGroupEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.UserGroupFilter;
import cz.clovekvtisni.coordinator.server.service.UserGroupService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.util.CloneTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 7.12.12
 */
@Service("userGroupService")
public class UserGroupServiceImpl extends AbstractEntityServiceImpl implements UserGroupService {

    @Autowired
    private UserInEventService userInEventService;

    @Autowired
    private UserService userService;

    @Override
    public UserGroupEntity findById(Long id, long flags) {
        UserGroupEntity entity = ofy().load().key(Key.create(UserGroupEntity.class, id)).get();

        if (entity != null)
            populate(Arrays.asList(new UserGroupEntity[]{entity}), flags);

        return entity;
    }

    private void populate(Collection<UserGroupEntity> entities, long flags) {
    }

    @Override
    public ResultList<UserGroupEntity> findByFilter(UserGroupFilter filter, int limit, String bookmark, long flags) {
        if (filter.getOrder() == null)
            filter.setOrder("id");
        return ofy().findByFilter(filter, bookmark, limit);
    }

    @Override
    public List<UserGroupEntity> findByIds(long flags, Long... ids) {
        if (ids == null)
            return null;

        Set<Key<UserGroupEntity>> keys = new HashSet<Key<UserGroupEntity>>(ids.length);
        for (Long id : ids) {
            if (id != null)
                keys.add(Key.create(UserGroupEntity.class, id));
        }

        Map<Key<UserGroupEntity>, UserGroupEntity> entityMap = ofy().get(keys);
        populate(entityMap.values(), flags);

        return new ArrayList<UserGroupEntity>(entityMap.values());

    }

    @Override
    public UserGroupEntity createUserGroup(final UserGroupEntity entity) {
        return ofy().transact(new Work<UserGroupEntity>() {
            @Override
            public UserGroupEntity run() {
                logger.debug("creating " + entity);

                entity.setId(null);
                updateSystemFields(entity, null);
                ofy().put(entity);

                systemService.saveUniqueIndexOwner(ofy(), UniqueIndexEntity.Property.USER_GROUP_NAME, uniqueKeyValue(entity), entity.getKey());

                return entity;
            }
        });
    }

    private String uniqueKeyValue(UserGroupEntity entity) {
        return entity.getEventId().toString() + "~" + entity.getName();
    }

    @Override
    public UserGroupEntity updateUserGroup(UserGroupEntity updated) {
        final UserGroupEntity group = CloneTool.deepClone(updated);
        final UserGroupEntity old = findById(group.getId(), 0l);
        logger.debug("updating " + group);
        return ofy().transact(new Work<UserGroupEntity>() {
            @Override
            public UserGroupEntity run() {
                updateSystemFields(group, old);
                systemService.deleteUniqueIndexOwner(ofy(), UniqueIndexEntity.Property.EMAIL, uniqueKeyValue(old));
                systemService.saveUniqueIndexOwner(ofy(), UniqueIndexEntity.Property.EMAIL, uniqueKeyValue(group), group.getKey());

                UserGroupEntity created = ofy().put(group);

                return created;
            }
        });
    }

    @Override
    public void deleteUserGroup(UserGroupEntity entity) {
        // TODO
    }

    /** TODO cache? */
    @Override
    public List<UserGroupEntity> findByEventId(Long eventId, long flags) {
        UserGroupFilter groupFilter = new UserGroupFilter();
        groupFilter.setOrder("name");
        groupFilter.setEventIdVal(eventId);
        return findByFilter(groupFilter, 0, null, flags).getResult();
    }

    @Override
    public void addUsersToGroup(UserGroupEntity entity, Long... userIds) {
        List<UserEntity> users = userService.findByIds(0l, userIds);
        for (UserEntity user : users) {
            UserInEventEntity inEvent = userInEventService.findById(entity.getEventId(), user.getId(), 0l);
            if (inEvent == null)
                continue;
            Set<Long> groupIdList = new HashSet<Long>();
            if (inEvent.getGroupIdList() != null) {
                groupIdList.addAll(Arrays.asList(inEvent.getGroupIdList()));
            }
            if (groupIdList.contains(entity.getId()))
                continue;
            groupIdList.add(entity.getId());
            inEvent.setGroupIdList(groupIdList.toArray(new Long[0]));
            userInEventService.update(inEvent);
        }
    }
}
