package cz.clovekvtisni.coordinator.server.service;

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

    @FilterResult("#helper.canRead(#entity)")
    PoiEntity findById(Long id, long flags);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<PoiEntity> findByFilter(PoiFilter filter, int limit, String bookmark, long flags);

    //@CheckPermission("#helper.canCreate(#entity)") TODO
    PoiEntity createPoi(PoiEntity entity);

    //@CheckPermission("#helper.canUpdate(#entity)") TODO
    PoiEntity updatePoi(PoiEntity entity);

    @CheckPermission("#helper.canDelete(#entity)")
    void deletePoi(PoiEntity entity);
}
