package cz.clovekvtisni.coordinator.server.util;

import cz.clovekvtisni.coordinator.domain.AbstractModifiableEntity;
import cz.clovekvtisni.coordinator.server.domain.AbstractPersistentEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 22.11.12
 */
public class EntityTool {

    public <T extends AbstractModifiableEntity, E extends AbstractPersistentEntity<T, E>> List<T> buildTargetEntities(Collection<E> source) {
        if (source == null) return null;
        List<T> result = new ArrayList<T>(source.size());

        for (E entity : source) {
            result.add(entity.buildTargetEntity());
        }

        return result;
    }
}
