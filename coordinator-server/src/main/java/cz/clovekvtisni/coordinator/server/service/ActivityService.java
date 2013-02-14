package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.server.domain.ActivityEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.security.FilterResult;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
public interface ActivityService extends Service {

    public static long FLAG_FETCH_ALL = 1;

    @FilterResult("#helper.canRead(#entity)")
    ResultList<ActivityEntity> find(Long eventId, Long poiId, Long userId, Long changedBy, long flags);

    void log(ActivityEntity e);

}
