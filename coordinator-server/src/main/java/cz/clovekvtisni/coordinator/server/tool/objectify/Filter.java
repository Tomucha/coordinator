package cz.clovekvtisni.coordinator.server.tool.objectify;

import java.io.Serializable;

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

    private AfterLoadCallback afterLoadCallback;

    public abstract Class<T> getEntityClass();

    public void setAfterLoadCallback(AfterLoadCallback afterLoadCallback) {
        this.afterLoadCallback = afterLoadCallback;
    }

    public boolean accept(T entity) {
        if (afterLoadCallback != null)
            return afterLoadCallback.accept(entity);
        else
            return true;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
