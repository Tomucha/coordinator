package cz.clovekvtisni.coordinator.server.tool.objectify;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"serial"})
public abstract class Filter<T> implements Serializable {

    public interface AfterLoadCallback<T> {
        public boolean accept(T entity);
    }

    public static enum Operator implements Serializable {
        EQ("="), NOT_EQ("!="), GT(">"), GE(">="), LT("<"), LE("<="), IN("in");

        private String op;

        Operator(String op) {
            this.op = op;
        }

        public String renderCondition(Object value) {
            // TODO collection
            return value + " " + op;
        }
    }

    private String order;

    private List<AfterLoadCallback> afterLoadCallbacks;

    public abstract Class<T> getEntityClass();

    public void addAfterLoadCallback(AfterLoadCallback afterLoadCallback) {
        if (afterLoadCallbacks == null)
            afterLoadCallbacks = new ArrayList<AfterLoadCallback>();
        this.afterLoadCallbacks.add(afterLoadCallback);
    }

    public boolean accept(T entity) {
        if (afterLoadCallbacks != null) {
            for (AfterLoadCallback afterLoadCallback : afterLoadCallbacks) {
                if (!afterLoadCallback.accept(entity))
                    return false;
            }
        }

        return true;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
