package cz.clovekvtisni.coordinator.server.web.model;

import cz.clovekvtisni.coordinator.server.domain.PoiEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.12.12
 */
public class PoiForm extends PoiEntity {

    public List<Long> getAssignedUsers() {
        Long[] users = getUserIdList();
        if (users == null)
            return new ArrayList<Long>(0);
        List<Long> res = new ArrayList<Long>(users.length);
        for (Long id : users) {
            if (id != null)
                res.add(id);
        }
        return res;
    }

    public void setAssignedUsers(List<Long> assignedUsers) {
        setUserIdList(assignedUsers != null ? assignedUsers.toArray(new Long[0]) : null);
    }
}
