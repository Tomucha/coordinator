package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.util.ValueTool;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/14/11
 * Time: 10:41 AM
 */
public class ResultList<T extends Serializable> implements Serializable, Iterable<T> {

    private static final long serialVersionUID = -1641989769271772754L;

    private List<T> result;
    private String bookmark;

    public ResultList(List<T> result, String bookmark) {
        this.result = result;
        this.bookmark = bookmark;
    }

    public List<T> getResult() {
        return result;
    }

    public int getResultSize() {
        return result == null ? 0 : result.size();
    }

    public String getBookmark() {
        return bookmark;
    }

    public boolean isNext() {
        return bookmark != null;
    }

    public T firstResult() {
        return ValueTool.isEmpty(result) ? null : result.get(0);
    }

    public T singleResult() {
        if (ValueTool.isEmpty(result)) {
            return null;
        }

        if (result.size() > 1) {
            throw new IllegalStateException("more results exist");
        }

        return result.get(0);
    }

    @Override
    public Iterator<T> iterator() {
        return result == null ? null : result.iterator();
    }

    @Override
    public String toString() {
        return "ResultList{" +
                "size=" + getResultSize() +
                ", bookmark='" + bookmark + '\'' +
                '}';
    }
}
