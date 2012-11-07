package cz.clovekvtisni.coordinator.server.tool.objectify;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.ObjectifyWrapper;
import cz.clovekvtisni.coordinator.util.ValueTool;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 7.11.12
 */
public class MaObjectify extends ObjectifyWrapper {

    public interface ResultFilter<T> {
        boolean accept(T entity);
    }

    public MaObjectify(Objectify ofy) {
        super(ofy);
    }

    public <T extends Serializable> ResultList<T> getResult(Class<T> entityClass, Filter filter, String bookmark, int limit, ResultFilter<T> resultFilter) {
        Query<T> query = query(entityClass);

        if (filter != null) {
            populateQueryFromFilter(query, filter);
            if (filter.getOrder() != null) {
                query.order(filter.getOrder());
            }
        }

        if (!ValueTool.isEmpty(bookmark)) {
            query.startCursor(Cursor.fromWebSafeString(bookmark));
        }

        QueryResultIterator<T> iterator = query.iterator();
        List<T> entities = new ArrayList<T>();
        while (iterator.hasNext()) {
            T entity = iterator.next();
            if (resultFilter != null && !resultFilter.accept(entity)) {
                continue;
            }
            entities.add(entity);
            if (--limit <= 0) {
                return new ResultList<T>(entities, iterator.getCursor().toWebSafeString());
            }
        }

        return new ResultList<T>(entities, null);
    }

    private <T> void populateQueryFromFilter(Query<T> query, Filter filter) {
        if (filter == null)
            return;

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
                                query.filter(baseName + " =", value);
                                break;
                        }
                    }
                }
            }
        }
    }
}
