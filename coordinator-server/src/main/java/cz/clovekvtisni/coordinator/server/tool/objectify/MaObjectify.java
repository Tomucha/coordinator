package cz.clovekvtisni.coordinator.server.tool.objectify;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.QueryKeys;
import com.googlecode.objectify.util.cmd.ObjectifyWrapper;
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

    // objectify is not thread-safe
    private static boolean inTransaction;

    public MaObjectify(Objectify ofy) {
        super(ofy);
    }

    public <T extends Serializable> List<Key<T>> findKeysByFilter(Filter<T> filter) {
        Query<T> query = load().type(filter.getEntityClass());

        query = populateQueryFromFilter(query, filter);
        QueryKeys<T> keys = query.keys();

        List<Key<T>> asList = new ArrayList<Key<T>>();
        for (Key<T> key : keys) {
            asList.add(key);
        }

        return asList;
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
        BeanWrapperImpl dstWrapper = new BeanWrapperImpl(filter.getEntityClass());
        PropertyDescriptor[] dstPropertyDescriptors = dstWrapper.getPropertyDescriptors();
        List<String> entityProperties = new ArrayList<String>(dstPropertyDescriptors.length);
        for (PropertyDescriptor dstPropertyDescriptor : dstPropertyDescriptors) {
            entityProperties.add(dstPropertyDescriptor.getName());
        }

        final PropertyDescriptor[] srcPropertyDescriptors = sourceWrapper.getPropertyDescriptors();
        for (PropertyDescriptor srcPropertyDescriptor : srcPropertyDescriptors) {
            final String srcPropertyName = srcPropertyDescriptor.getName();
            if (srcPropertyName.endsWith("Val")) {
                String baseName = srcPropertyName.substring(0, srcPropertyName.length() - 3);
                if (!entityProperties.contains(baseName))
                    continue;
                String operatorName = baseName + "Op";
                if (sourceWrapper.isReadableProperty(srcPropertyName) && sourceWrapper.isReadableProperty(operatorName)) {
                    final Object value = sourceWrapper.getPropertyValue(srcPropertyName);
                    if (value != null) {
                        Filter.Operator operator = (Filter.Operator) sourceWrapper.getPropertyValue(operatorName);
                        if (operator == null)
                            operator = Filter.Operator.EQ;
                        query = query.filter(operator.renderCondition(baseName), value);
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

    public <T> Map<Key<T>, T> get(Set<Key<T>> keys) {
        return load().keys(keys);
    }

    public <T> T get(Key<T> key) {
        return load().key(key).get();
    }

    /** enables pseudo nested transaction */
    @Override
    public <R> R transact(Work<R> work) {
        if (inTransaction)
            return work.run();
        else {
            R result;
            try {
                inTransaction = true;
                result = super.transact(work);

            } finally {
                inTransaction = false;
            }
            return result;
        }
    }
}
