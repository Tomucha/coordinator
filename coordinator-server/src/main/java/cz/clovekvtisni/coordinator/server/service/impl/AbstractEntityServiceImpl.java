package cz.clovekvtisni.coordinator.server.service.impl;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import cz.clovekvtisni.coordinator.server.domain.AbstractPersistentEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:54 PM
 */
public class AbstractEntityServiceImpl extends AbstractServiceImpl {

    @Autowired
    protected SystemService systemService;

    @Autowired
    protected AppContext appContext;

    protected <T extends AbstractPersistentEntity> List<T> mergeEntities(T[] oldList, T[] newList) {
        if (oldList == null)
            return Arrays.asList(newList);
        Map<Long, T> oldMap = new HashMap<Long, T>(oldList.length);
        for (T entity : oldList) {
            if (entity != null && entity.getId() != null)
                oldMap.put(entity.getId(), entity);
        }

        List<T> merged = new ArrayList<T>();
        for (T entity : newList) {
            if (entity.isDeleted() && entity.isNew())
                continue;
            if (!entity.isNew()) {
                T oldEntity = oldMap.get(entity.getId());
                if (oldEntity != null) {
                    entity.setCreatedDate(oldEntity.getCreatedDate());
                    oldMap.remove(oldEntity.getId());
                }
            }
            merged.add(entity);
        }
        Date now = new Date();
        for (T entity : oldMap.values()) {
            entity.setDeletedDate(now);
            merged.add(entity);
        }

        return merged;
    }
}
