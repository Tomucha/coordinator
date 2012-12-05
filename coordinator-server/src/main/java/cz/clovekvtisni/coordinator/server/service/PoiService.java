package cz.clovekvtisni.coordinator.server.service;

import com.sun.xml.internal.bind.v2.TODO;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.security.FilterResult;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
public interface PoiService extends Service {

    public static final long FLAG_FETCH_FROM_CONFIG = 1l;

    @FilterResult("#helper.canRead(#entity)")
    PoiEntity findById(Long id, long flags);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<PoiEntity> findByFilter(PoiFilter filter, int limit, String bookmark, long flags);

    @CheckPermission("#helper.canCreate(#p0)")
    PoiEntity createPoi(PoiEntity entity);

    @CheckPermission("#helper.canUpdate(#p0)")
    PoiEntity updatePoi(PoiEntity entity);

    @CheckPermission("#helper.canDelete(#p0)")
    void deletePoi(PoiEntity entity);
}
