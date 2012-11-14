package cz.clovekvtisni.coordinator.server.tool.objectify;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.util.cmd.ObjectifyWrapper;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.util.ValueTool;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 7.11.12
 */
public class MaObjectify extends ObjectifyWrapper<MaObjectify, ObjectifyFactory> {

    public MaObjectify(Objectify ofy) {
        super(ofy);
    }

    public <T extends Serializable> ResultList<T> findByFilter(Filter<T> filter, String bookmark, int limit) {

        Query<T> query = load().type(filter.getEntityClass());

        query = populateQueryFromFilter(query, filter);
        if (filter.getOrder() != null) {
            query = query.order(filter.getOrder());
        }

        boolean limited = limit > 0;
        if (limited)
            query = query.limit(limit);

        if (!ValueTool.isEmpty(bookmark)) {
            Cursor cursor = Cursor.fromWebSafeString(bookmark);
            query = query.startAt(cursor);
        }

        QueryResultIterator<T> iterator = query.iterator();
        List<T> entities = new ArrayList<T>();
        while (iterator.hasNext()) {
            T entity = iterator.next();
            if (!filter.accept(entity)) {
                continue;
            }
            entities.add(entity);
            if (limited && --limit <= 0) {
                return new ResultList<T>(entities, iterator.getCursor().toWebSafeString());
            }
        }

        return new ResultList<T>(entities, null);
    }

    private <T> Query<T> populateQueryFromFilter(Query<T> query, Filter filter) {
        if (filter == null)
            return query;

        BeanWrapper sourceWrapper = new BeanWrapperImpl(filter);

        final PropertyDescriptor[] srcPropertyDescriptors = sourceWrapper.getPropertyDescriptors();
        for (PropertyDescriptor srcPropertyDescriptor : srcPropertyDescriptors) {
            final String srcPropertyName = srcPropertyDescriptor.getName();
            if (srcPropertyName.endsWith("Val")) {
                String baseName = srcPropertyName.substring(0, srcPropertyName.length() - 3);
                String operatorName = baseName + "Op";
                if (sourceWrapper.isReadableProperty(srcPropertyName) && sourceWrapper.isReadableProperty(operatorName)) {
                    Filter.Operator operator = (Filter.Operator) sourceWrapper.getPropertyValue(operatorName);
                    if (operator == null) {
                        operator = Filter.Operator.EQ;
                    }
                    final Object value = sourceWrapper.getPropertyValue(srcPropertyName);
                    if (value != null) {
                        switch (operator) {
                            case EQ:
                                query = query.filter(baseName, value);
                                break;
                        }
                    }
                }
            }
        }

        return query;
    }

    // heritage code

    public <T> T put(T entity) {
        save().entity(entity).now();
        return entity;
    }

    public <T> T delete(T entity) {
        delete().entity(entity).now();
        return entity;
    }

    public Map<Key<EventEntity>, EventEntity> get(Set<Key<EventEntity>> keys) {
        return load().keys(keys);
    }

    public PoiEntity get(Key<PoiEntity> poiEntityKey) {
        return load().key(poiEntityKey).get();
    }
}
