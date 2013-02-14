package cz.clovekvtisni.coordinator.server.service.impl;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.BoundingBox;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;
import cz.clovekvtisni.coordinator.domain.config.PoiCategory;
import cz.clovekvtisni.coordinator.domain.config.Workflow;
import cz.clovekvtisni.coordinator.domain.config.WorkflowTransition;
import cz.clovekvtisni.coordinator.server.domain.ActivityEntity;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.service.ActivityService;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 10.11.12
 */
@Service("poiService")
public class PoiServiceImpl extends AbstractServiceImpl implements PoiService {

    @Autowired
    private CoordinatorConfig config;

    @Autowired
    private AuthorizationTool authorizationTool;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserInEventService userInEventService;

    @Override
    public PoiEntity findById(Long id, long flags) {
        PoiEntity poi = ofy().get(Key.create(PoiEntity.class, id));

        populate(Arrays.asList(new PoiEntity[] {poi}), flags);

        return poi;
    }

    private void populate(Collection<PoiEntity> entities, long flags) {
        Map<String,PoiCategory> categoryMap = config.getPoiCategoryMap();
        for (PoiEntity poi : entities) {
            if (poi.getPoiCategoryId() != null) {
                poi.setPoiCategory(categoryMap.get(poi.getPoiCategoryId()));
            }
        }

        Map<String,Workflow> workflowMap = config.getWorkflowMap();
        for (PoiEntity poi : entities) {
            if (poi.getWorkflowId() != null) {
                Workflow workflow = workflowMap.get(poi.getWorkflowId());
                poi.setWorkflow(workflow);
                if (workflow != null && poi.getWorkflowStateId() != null) {
                    poi.setWorkflowState(workflow.getStateMap().get(poi.getWorkflowStateId()));
                }
            }
        }
    }

    @Override
    public ResultList<PoiEntity> findByFilter(PoiFilter filter, int limit, String bookmark, long flags) {
        ResultList<PoiEntity> result = ofy().findByFilter(filter, bookmark, limit);
        populate(result.getResult(), flags);

        return result;
    }

    @Override
    public List<PoiEntity> findByEventAndBox(long eventId, double latN, double lonE, double latS, double lonW, long flags) {
        // Transform this to a bounding box
        BoundingBox bb = new BoundingBox(latN, lonE, latS, lonW);

        // Calculate the geocells list to be used in the queries (optimize list of cells that complete the given bounding box)
        List<String> cells = GeocellManager.bestBboxSearchCells(bb, null);

        Query<PoiEntity> q = ofy().load().type(PoiEntity.class).filter("eventId", eventId).filter("geoCells IN", cells);
        List<PoiEntity> result = q.list();

        populate(result, flags);
        return result;
    }

    @Override
    public PoiEntity createPoi(final PoiEntity entity) {
        logger.debug("creating " + entity);

        return ofy().transact(new Work<PoiEntity>() {
            @Override
            public PoiEntity run() {
                entity.setId(null);
                updateSystemFields(entity, null);

                // let's enforce workflow

                PoiCategory c = config.getPoiCategoryMap().get(entity.getPoiCategoryId());
                Workflow w = config.getWorkflowMap().get(c.getWorkflowId());
                entity.setWorkflow(w);
                if (w != null) {
                    entity.setWorkflowState(w.getStartState());
                }

                ofy().put(entity);


                ActivityEntity a = new ActivityEntity();
                a.setPoiId(entity.getId());
                a.setEventId(entity.getEventId());
                a.setType(ActivityEntity.ActivityType.CREATED_POI);
                activityService.log(a);

                if (w != null) {
                    a.setId(null); // let's insert once again
                    a.setType(ActivityEntity.ActivityType.WORKFLOW_START);
                    activityService.log(a);
                }


                // FIXME: visibility, workflow state, assigned

                return entity;
            }
        });
    }

    @Override
    public PoiEntity updatePoi(final PoiEntity entity) {
        return ofy().transact(new Work<PoiEntity>() {
            @Override
            public PoiEntity run() {
                PoiEntity old = ofy().get(entity.getKey());
                updateSystemFields(entity, old);
                entity.setWorkflowId(old.getWorkflowId());
                ofy().put(entity);

                return entity;
            }
        });
    }

    @Override
    public PoiEntity assignUser(final PoiEntity poi, final Long userId) {
        return ofy().transact(new Work<PoiEntity>() {
            @Override
            public PoiEntity run() {
                PoiEntity old = ofy().get(poi.getKey());
                UserInEventEntity user = ofy().get(UserInEventEntity.createKey(userId, old.getEventId()));
                if (user == null) throw new IllegalStateException("No such user in event: "+userId);
                old.getUserIdList().add(user.getUserId());
                updateSystemFields(old, old);
                ofy().put(old);

                ActivityEntity a = new ActivityEntity();
                a.setUserId(userId);
                a.setType(ActivityEntity.ActivityType.ASSIGNED);
                a.setPoiId(poi.getId());
                a.setEventId(poi.getEventId());
                activityService.log(a);

                return old;
            }
        });
    }

    @Override
    public PoiEntity unassignUser(final PoiEntity poi, final Long userId) {
        return ofy().transact(new Work<PoiEntity>() {
            @Override
            public PoiEntity run() {
                PoiEntity old = ofy().get(poi.getKey());
                UserInEventEntity user = ofy().get(UserInEventEntity.createKey(userId, old.getEventId()));
                if (user == null) throw new IllegalStateException("No such user in event: "+userId);
                old.getUserIdList().remove(user.getUserId());
                updateSystemFields(old, old);
                ofy().put(old);

                ActivityEntity a = new ActivityEntity();
                a.setUserId(userId);
                a.setType(ActivityEntity.ActivityType.UNASSIGNED);
                a.setPoiId(poi.getId());
                a.setEventId(poi.getEventId());
                activityService.log(a);

                return old;
            }
        });
    }

    @Override
    public void deletePoi(PoiEntity entity, long flags) {
        entity.setDeletedDate(new Date());
        updatePoi(entity);
    }

    @Override
    public ResultList<PoiEntity> findLast(String organizationId) {
        PoiFilter filter = new PoiFilter();
        filter.setOrganizationIdVal(organizationId);
        filter.setOrder("-createdDate");
        return findByFilter(filter, LAST_POI_LIST_LENGTH, null, 0l);
    }

    @Override
    public ResultList<PoiEntity> findLastByEventId(Long eventId) {
        PoiFilter filter = new PoiFilter();
        filter.setEventIdVal(eventId);
        filter.setOrder("-createdDate");
        return findByFilter(filter, LAST_POI_LIST_LENGTH, null, 0l);
    }

    @Override
    public PoiEntity transitWorkflowState(PoiEntity entity, String transitionId) {
        if (entity == null || transitionId == null)
            return entity;
        if (entity.getWorkflowStateId() == null || entity.getWorkflowState().getTransitions() == null)
            throw new IllegalArgumentException("no transition=" + transitionId + " in workflow state=" + entity.getWorkflowStateId());
        WorkflowTransition transition = entity.getWorkflowState().getTransitionMap().get(transitionId);
        if (transition == null)
            throw new IllegalArgumentException("no transition=" + transitionId + " in workflow state=" + entity.getWorkflowStateId());
        entity.setWorkflowState(null);
        entity.setWorkflowStateId(transition.getToStateId());
        return updatePoi(entity);
    }

}
