package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.security.FilterResult;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
public interface PoiService extends Service {

    public static final int LAST_POI_LIST_LENGTH = 30;

    @FilterResult("#helper.canRead(#entity)")
    PoiEntity findById(Long id, long flags);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<PoiEntity> findByFilter(PoiFilter filter, int limit, String bookmark, long flags);

    @CheckPermission("#helper.canCreate(#p0)")
    @CacheEvict(value = {"lastPoiList", "lastEventPoiList"}, allEntries = true)
    PoiEntity createPoi(PoiEntity entity);

    @CheckPermission("#helper.canUpdate(#p0)")
    @CacheEvict(value = {"lastPoiList", "lastEventPoiList"}, allEntries = true)
    PoiEntity updatePoi(PoiEntity entity);

    @CheckPermission("#helper.canDelete(#p0)")
    @CacheEvict(value = {"lastPoiList", "lastEventPoiList"}, allEntries = true)
    void deletePoi(PoiEntity entity, long flags);

    @FilterResult("#helper.canRead(#entity)")
    @Cacheable(value = "lastPoiList")
    ResultList<PoiEntity> findLast(String organizationId);

    @FilterResult("#helper.canRead(#entity)")
    @Cacheable(value = "lastEventPoiList")
    ResultList<PoiEntity> findLastByEventId(Long eventId);

    @CheckPermission("#helper.canUpdate(#p0)")
    @CacheEvict(value = {"lastPoiList", "lastEventPoiList"}, allEntries = true)
    PoiEntity startWorkflow(PoiEntity entity);

    @CheckPermission("#helper.canUpdate(#p0)")
    @CacheEvict(value = {"lastPoiList", "lastEventPoiList"}, allEntries = true)
    PoiEntity transitWorkflowState(PoiEntity entity, String transitionId);

    @CheckPermission("#helper.canUpdate(#p0)")
    PoiEntity assignUser(PoiEntity poi, Long userId);

    @CheckPermission("#helper.canUpdate(#p0)")
    PoiEntity unassignUser(PoiEntity poi, Long userInEventId);

}
