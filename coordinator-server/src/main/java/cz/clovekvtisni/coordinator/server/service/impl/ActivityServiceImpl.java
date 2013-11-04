package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import cz.clovekvtisni.coordinator.domain.config.PoiCategory;
import cz.clovekvtisni.coordinator.domain.config.Workflow;
import cz.clovekvtisni.coordinator.server.domain.ActivityEntity;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.service.ActivityService;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tomucha
 * Date: 14.02.13
 * Time: 23:16
 * To change this template use File | Settings | File Templates.
 */
@Service("activityService")
public class ActivityServiceImpl extends AbstractEntityServiceImpl implements ActivityService {

    @Autowired
    private PoiService poiService;

    @Autowired
    private UserService userService;

    @Override
    public ResultList<ActivityEntity> find(Long eventId, Long poiId, Long userId, Long changedBy, long flags) {
        Query q = ofy().load().type(ActivityEntity.class);
        if (eventId != null) {
            q = q.filter("eventId", eventId);
        }
        if (poiId != null) {
            q = q.filter("poiId", poiId);
        }
        if (userId != null) {
            q = q.filter("userId", userId);
        }
        if (changedBy != null) {
            q = q.filter("changedBy", changedBy);
        }
        q = q.limit(30);
        q = q.order("-changeDate");

        List<ActivityEntity> result = q.list();

        if ((FLAG_FETCH_ALL & flags) != 0) {
            for (ActivityEntity a: result) {
                if (a.getPoiId() != null) {
                   a.setPoiEntity(poiService.findById(a.getPoiId(), 0));
                }
                if (a.getUserId() != null) {
                   a.setUserEntity(userService.findById(a.getUserId(), 0));
                }
                if (a.getChangedBy() != null) {
                    a.setChangedByEntity(userService.findById(a.getChangedBy(), 0));
                }
            }

        }
        return new ResultList<ActivityEntity>(result, null);
    }

    @Override
    public void log(ActivityEntity e) {
        e.setChangeDate(new Date());
        e.setChangedBy(appContext.getLoggedUser().getId());
        ofy().put(e);
    }
}
