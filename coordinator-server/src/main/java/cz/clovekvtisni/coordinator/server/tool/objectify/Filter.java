package cz.clovekvtisni.coordinator.server.tool.objectify;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings({"serial"})
public abstract class Filter implements Serializable {

    public static enum Operator implements Serializable {
        EQ,
        /* GT, GE, LT, LE, LIKE, ILIKE,
        NOT_EQ, NOT_LIKE, NOT_ILIKE,*/
    }

    private String order;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
