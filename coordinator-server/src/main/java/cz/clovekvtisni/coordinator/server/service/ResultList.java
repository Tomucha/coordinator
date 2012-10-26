package cz.clovekvtisni.coordinator.server.service;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/14/11
 * Time: 10:41 AM
 */
public class ResultList<T extends Serializable> implements Serializable {

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

    public String getBookmark() {
        return bookmark;
    }

    public boolean isNext() {
        return bookmark != null;
    }
}
