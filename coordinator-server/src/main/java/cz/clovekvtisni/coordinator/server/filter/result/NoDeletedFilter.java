package cz.clovekvtisni.coordinator.server.filter.result;

import cz.clovekvtisni.coordinator.domain.AbstractModifiableEntity;
import cz.clovekvtisni.coordinator.server.domain.AbstractPersistentEntity;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.tool.objectify.MaObjectify;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 7.11.12
 */
public class NoDeletedFilter<T extends AbstractPersistentEntity> implements MaObjectify.ResultFilter<T> {
    @Override
    public boolean accept(AbstractPersistentEntity entity) {
        return !entity.isDeleted();
    }
}
