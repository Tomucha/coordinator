package cz.clovekvtisni.coordinator.server.tool.objectify;

import cz.clovekvtisni.coordinator.server.domain.AbstractPersistentEntity;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 12.11.12
 */
public abstract class NoDeletedFilter<T extends AbstractPersistentEntity> extends Filter<T> {
    protected NoDeletedFilter() {
        includeDeleted(false);
    }

    public void includeDeleted(boolean include) {
        if (include)
            setAfterLoadCallback(new AfterLoadCallback<T>() {
                @Override
                public boolean accept(T entity) {
                    // not null test due to latency of app engine
                    return entity != null && !entity.isDeleted();
                }
            });
        else
            setAfterLoadCallback(null);
    }
}
