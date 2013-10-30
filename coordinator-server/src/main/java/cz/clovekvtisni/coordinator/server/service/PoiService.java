package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.security.FilterResult;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
public interface PoiService extends Service {

    public static final int LAST_POI_LIST_LENGTH = 30;

    public static final long FLAG_DISABLE_FORCE_SINGLE_ASSIGN = 1l;

    @FilterResult("#helper.canRead(#entity)")
    PoiEntity findById(Long id, long flags);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<PoiEntity> findByFilter(PoiFilter filter, int limit, String bookmark, long flags);

    @FilterResult("#helper.canRead(#entity)")
    List<PoiEntity> findByFilterAndBox(PoiFilter filter, double latN, double lonE, double latS, double lonW, long flags);

    @CheckPermission("#helper.canCreate(#p0)")
    PoiEntity createPoi(PoiEntity entity);

    @CheckPermission("#helper.canUpdate(#p0)")
    PoiEntity updatePoi(PoiEntity entity);

    @CheckPermission("#helper.canDelete(#p0)")
    void deletePoi(PoiEntity entity, long flags);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<PoiEntity> findLast(String organizationId);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<PoiEntity> findLastByEventId(Long eventId);

    // FIXME: prava, ted jsou jen pro Backend ale musi to jit posunovat i z mobilu, kdyz to na me je assignovane
    @CheckPermission("#helper.canDo(new cz.clovekvtisni.coordinator.server.security.permission.TransitionPermission(#p0, #p1))")
    PoiEntity transitWorkflowState(PoiEntity entity, String transitionId, String comment, long flags);

    @CheckPermission("#helper.canUpdate(#p0)")
    PoiEntity assignUser(PoiEntity poi, Long userId);

    @CheckPermission("#helper.canUpdate(#p0)")
    PoiEntity assignUserExclusive(PoiEntity poi, Long userId);

    @CheckPermission("#helper.canUpdate(#p0)")
    PoiEntity unassignUser(PoiEntity poi, Long userInEventId);

    List<PoiEntity> findPoisForExport(String organizationId, Long eventId);
}
