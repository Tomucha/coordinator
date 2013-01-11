package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import cz.clovekvtisni.coordinator.domain.config.PoiCategory;
import cz.clovekvtisni.coordinator.domain.config.Workflow;
import cz.clovekvtisni.coordinator.domain.config.WorkflowState;
import cz.clovekvtisni.coordinator.domain.config.WorkflowTransition;
import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    public PoiEntity createPoi(final PoiEntity entity) {
        logger.debug("creating " + entity);

        return ofy().transact(new Work<PoiEntity>() {
            @Override
            public PoiEntity run() {
                entity.setId(null);
                updateSystemFields(entity, null);
                ofy().put(entity);

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
                ofy().put(entity);

                return entity;
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
    public PoiEntity startWorkflow(PoiEntity entity) {
        Workflow workflow = entity.getWorkflow();
        if (workflow == null)
            return entity;
        if (!authorizationTool.isCanBeStartedBy(entity, getLoggedUser()))
            throw MaPermissionDeniedException.permissionDenied();
        WorkflowState startState = workflow.getStartState();
        entity.setWorkflowStateId(startState != null ? startState.getId() : null);
        entity.setWorkflowState(startState);
        return updatePoi(entity);
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
