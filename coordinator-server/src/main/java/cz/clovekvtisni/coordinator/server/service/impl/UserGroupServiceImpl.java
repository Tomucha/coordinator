package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import cz.clovekvtisni.coordinator.server.domain.UniqueIndexEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserGroupEntity;
import cz.clovekvtisni.coordinator.server.filter.UserGroupFilter;
import cz.clovekvtisni.coordinator.server.service.UserGroupService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.util.CloneTool;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 7.12.12
 */
public class UserGroupServiceImpl extends AbstractEntityServiceImpl implements UserGroupService {

    @Override
    public UserGroupEntity findById(Long id, long flags) {
        UserGroupEntity entity = ofy().load().key(Key.create(UserGroupEntity.class, id)).get();

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
}
