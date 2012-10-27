package cz.clovekvtisni.coordinator.server.service;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.domain.UniqueIndexEntity;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:49 PM
 */
public interface SystemService extends Service {

    void saveUniqueIndexOwner(Objectify ofy, UniqueIndexEntity.Property property, String value, Key<? extends CoordinatorEntity> ownerKey);

    void deleteUniqueIndexOwner(Objectify ofy, UniqueIndexEntity.Property property, String value);

    <T> Key<T> findUniqueValueOwner(Objectify ofy, UniqueIndexEntity.Property property, String value);

    void initApplication();
}
