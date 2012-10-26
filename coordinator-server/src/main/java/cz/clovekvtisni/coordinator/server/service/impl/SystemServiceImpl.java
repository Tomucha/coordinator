package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.domain.UniqueIndexEntity;
import cz.clovekvtisni.coordinator.server.service.SystemService;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:53 PM
 */
@Service("systemService")
public class SystemServiceImpl extends AbstractServiceImpl implements SystemService {
    @Override
    public void saveUniqueIndexOwner(Objectify ofy, UniqueIndexEntity.Property property, String value, Key<? extends CoordinatorEntity> ownerKey) {
        assertCrossGroupTransaction(ofy);
        Key<UniqueIndexEntity> k = UniqueIndexEntity.createKey(property, value);
        UniqueIndexEntity saved = ofy.find(k);
        if (saved != null) {
            if (saved.getEntityKey().equals(ownerKey)) {
                // nothing to to do, we already know this
                return;
            } else {
                throw new IllegalStateException(ownerKey+ " is not an owner of "+k);
            }
        }
        UniqueIndexEntity index = new UniqueIndexEntity(k, ownerKey);
        ofy.put(index);
    }

    @Override
    public void deleteUniqueIndexOwner(Objectify ofy, UniqueIndexEntity.Property property, String value) {
        assertCrossGroupTransaction(ofy);
        Key<UniqueIndexEntity> k = UniqueIndexEntity.createKey(property, value);
        ofy.delete(k);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Key<T> findUniqueValueOwner(Objectify ofy, UniqueIndexEntity.Property property, String value) {
        Key<UniqueIndexEntity> k = UniqueIndexEntity.createKey(property, value);
        UniqueIndexEntity index =  ofy.find(k);
        if (index == null) return null;
        return (Key<T>) index.getEntityKey();
    }
}
