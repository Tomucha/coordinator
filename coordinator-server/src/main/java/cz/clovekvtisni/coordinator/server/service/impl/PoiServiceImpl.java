package cz.clovekvtisni.coordinator.server.service.impl;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.BoundingBox;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import cz.clovekvtisni.coordinator.domain.NotificationType;
import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.domain.config.PoiCategory;
import cz.clovekvtisni.coordinator.domain.config.Workflow;
import cz.clovekvtisni.coordinator.domain.config.WorkflowState;
import cz.clovekvtisni.coordinator.domain.config.WorkflowTransition;
import cz.clovekvtisni.coordinator.server.domain.*;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.service.ActivityService;
import cz.clovekvtisni.coordinator.server.service.NotificationService;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.workflow.WorkflowCallbackAccessor;
import cz.clovekvtisni.coordinator.server.workflow.callback.WorkflowCallback;
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
    private ActivityService activityService;

    @Autowired
    private UserInEventService userInEventService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private WorkflowCallbackAccessor callbackAccessor;

    @Autowired
    private AuthorizationTool authorizationTool;

    @Override
    public PoiEntity findById(Long id, long flags) {
        PoiEntity poi = ofy().get(Key.create(PoiEntity.class, id));

        if (poi != null)
            populate(Arrays.asList(new PoiEntity[] {poi}), flags);

        return poi;
    }

    private void populate(Collection<PoiEntity> entities, long flags) {
        Map<String,PoiCategory> categoryMap = config.getPoiCategoryMap();
        for (PoiEntity poi : entities) {
            if (poi != null && poi.getPoiCategoryId() != null) {
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
    public List<PoiEntity> findByFilterAndBox(PoiFilter filter, double latN, double lonE, double latS, double lonW, long flags) {
        BoundingBox bb = new BoundingBox(latN, lonE, latS, lonW);

        // Calculate the geocells list to be used in the queries (optimize list of cells that complete the given bounding box)
        List<String> cells = GeocellManager.bestBboxSearchCells(bb, null);

        filter.setGeoCellsVal(cells);
        filter.setGeoCellsOp(Filter.Operator.IN);

        ResultList<PoiEntity> result = ofy().findByFilter(filter, null, 0);
        populate(result.getResult(), flags);

        return result.getResult();
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

                if (entity.getPoiCategoryId() == null) throw new IllegalArgumentException("Null poiCategoryId");

                PoiCategory c = config.getPoiCategoryMap().get(entity.getPoiCategoryId());
                Workflow w = config.getWorkflowMap().get(c.getWorkflowId());
                entity.setWorkflow(w);
                if (w != null) {
                    entity.setWorkflowState(w.getStartState());
                }

                entity.setVisibleForRole(authorizationTool.findRolesWithReadPermission(entity));
                ofy().put(entity);

                if (w != null) {
                    entity.setPublicExport(
                            config.getPoiCategoryMap().get(entity.getPoiCategoryId()).isPublicExport() ? true :
                                w.getStateMap().get(entity.getWorkflowStateId()).isPublicExport()
                    );
                } else {
                    entity.setPublicExport(config.getPoiCategoryMap().get(entity.getPoiCategoryId()).isPublicExport());
                }

                ActivityEntity a = new ActivityEntity();
                a.setPoiId(entity.getId());
                a.setEventId(entity.getEventId());
                a.setType(ActivityEntity.ActivityType.CREATED_POI);
                activityService.log(a);

                if (w != null) {
                    a.setId(null); // let's insert once again
                    a.setType(ActivityEntity.ActivityType.WORKFLOW_START);
	                a.setWorkflowId(entity.getWorkflowId());
	                a.setWorkflowStateId(entity.getWorkflowStateId());
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

	            if (old.getPoiCategoryId().equals(entity.getPoiCategoryId())) {
	                entity.setWorkflowId(old.getWorkflowId());
		            // nedavat sem state ID metoda se pouziva i pro WF posun

	            } else {
		            // zmenila se kategorie
		            PoiCategory cat = config.getPoiCategoryMap().get(entity.getPoiCategoryId());
		            Workflow newW = config.getWorkflowMap().get(cat.getWorkflowId());
		            if (newW == null) {
			            entity.setWorkflowId(null);
			            entity.setWorkflowStateId(null);
		            } else {
		                entity.setWorkflow(newW);
		                entity.setWorkflowState(newW.getStartState());
			            ActivityEntity a = new ActivityEntity();
			            a.setPoiId(entity.getId());
			            a.setEventId(entity.getEventId());
			            a.setWorkflowId(newW.getId());
			            a.setWorkflowStateId(newW.getStartState().getId());
			            a.setType(ActivityEntity.ActivityType.WORKFLOW_START);
			            activityService.log(a);
		            }
	            }

	            entity.setVisibleForRole(authorizationTool.findRolesWithReadPermission(entity));
                Workflow workflow = config.getWorkflowMap().get(entity.getWorkflowId());
                if (workflow != null) {
                    entity.setPublicExport(
                            config.getPoiCategoryMap().get(entity.getPoiCategoryId()).isPublicExport() ? true :
                                    workflow.getStateMap().get(entity.getWorkflowStateId()).isPublicExport()
                    );
                } else {
                    entity.setPublicExport(config.getPoiCategoryMap().get(entity.getPoiCategoryId()).isPublicExport());
                }

                ofy().put(entity);

                ActivityEntity a = new ActivityEntity();
                a.setPoiId(entity.getId());
                a.setEventId(entity.getEventId());
                a.setType(ActivityEntity.ActivityType.MODIFIED_POI);
                activityService.log(a);


                return entity;
            }
        });
    }

    @Override
    public PoiEntity assignUser(final PoiEntity poi, final Long userId) {
        PoiEntity updatedPoi = ofy().transact(new Work<PoiEntity>() {
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

                UserInEventEntity u = userInEventService.findById(poi.getEventId(), userId, 0);
                u.setLastPoiDate(new Date());
                u.setLastPoiId(poi.getId());
                ofy().put(u);

                return old;
            }
        });

        notificationService.sendPoiNotification(NotificationType.ASSIGN, updatedPoi, userId);


        return updatedPoi;
    }

    @Override
    public PoiEntity unassignUser(final PoiEntity poi, final Long userId) {
        PoiEntity updatedPoi = ofy().transact(new Work<PoiEntity>() {
            @Override
            public PoiEntity run() {
                PoiEntity old = ofy().get(poi.getKey());
                UserInEventEntity user = ofy().get(UserInEventEntity.createKey(userId, old.getEventId()));
                if (user == null) throw new IllegalStateException("No such user in event: "+userId);
                old.getUserIdList().remove(user.getUserId());
                updateSystemFields(old, old);
                ofy().put(old);

                // odstraneni posledni poi
                if (user.getLastPoiId() != null && user.getLastPoiId().equals(poi.getId())) {
                    user.setLastPoiEntity(null);
                    user.setLastPoiId(null);
                    user.setLastPoiDate(null);
                    ofy().put(user);
                }

                ActivityEntity a = new ActivityEntity();
                a.setUserId(userId);
                a.setType(ActivityEntity.ActivityType.UNASSIGNED);
                a.setPoiId(poi.getId());
                a.setEventId(poi.getEventId());
                activityService.log(a);

                return old;
            }
        });

        notificationService.sendPoiNotification(NotificationType.UNASSIGN, updatedPoi, userId);

        return updatedPoi;
    }

    @Override
    public List<PoiEntity> findPoisForExport(String organizationId, Long eventId) {
        return ofy().load().type(PoiEntity.class).filter("organizationId", organizationId).filter("publicExport", true).filter("eventId", eventId).list();
    }

    @Override
    public PoiEntity assignUserExclusive(final PoiEntity poi, final Long userId) {
        return ofy().transact(new Work<PoiEntity>() {
            @Override
            public PoiEntity run() {
                PoiEntity old = ofy().get(poi.getKey());
                UserInEventEntity user = ofy().get(UserInEventEntity.createKey(userId, old.getEventId()));
                if (user == null) throw new IllegalStateException("No such user in event: "+userId);
                Set<Long> oldUserIdList = old.getUserIdList();
                HashSet<Long> newUserIdList = new HashSet<Long>(1);
                newUserIdList.add(user.getUserId());
                old.setUserIdList(newUserIdList);
                updateSystemFields(old, old);
                ofy().put(old);

                ActivityEntity a = new ActivityEntity();
                a.setUserId(userId);
                a.setType(ActivityEntity.ActivityType.ASSIGNED);
                a.setPoiId(poi.getId());
                a.setEventId(poi.getEventId());
                activityService.log(a);

                UserInEventEntity u = userInEventService.findById(poi.getEventId(), userId, 0);
                u.setLastPoiDate(new Date());
                u.setLastPoiId(poi.getId());
                ofy().put(u);

                // remove all other assigned users
                oldUserIdList.remove(userId);
                for (Long unassignedUserId : oldUserIdList) {
                    ActivityEntity ua = new ActivityEntity();
                    ua.setUserId(unassignedUserId);
                    ua.setType(ActivityEntity.ActivityType.UNASSIGNED);
                    ua.setPoiId(poi.getId());
                    ua.setEventId(poi.getEventId());
                    activityService.log(a);
                }

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
    public PoiEntity transitWorkflowState(final PoiEntity entity, final String transitionId, final String comment, final long flags) {
	    logger.info("Starting transition on "+entity+" to "+transitionId);
        if (entity == null || transitionId == null)
            return entity;
        if (entity.getWorkflowStateId() == null || entity.getWorkflowState().getTransitions() == null)
            throw new IllegalArgumentException("no transition=" + transitionId + " in workflow state=" + entity.getWorkflowStateId());
        final WorkflowTransition transition = entity.getWorkflowState().getTransitionMap().get(transitionId);
        if (transition == null)
            throw new IllegalArgumentException("no transition=" + transitionId + " in workflow state=" + entity.getWorkflowStateId());

        PoiEntity updated = ofy().transact(new Work<PoiEntity>() {
            @Override
            public PoiEntity run() {
                PoiEntity entityF;
                String onBeforeCallbackKey = transition.getOnBeforeTransition();
                if (onBeforeCallbackKey != null) {
                    WorkflowCallback beforeCallback = callbackAccessor.getCallbackByKey(onBeforeCallbackKey);
                    if (beforeCallback != null) {
                        logger.info("Running callback: "+beforeCallback);
                        boolean result = beforeCallback.onBeforeTransition(entity, transition);
                        if (!result)
                            return entity;
                        entityF = findById(entity.getId(), 0L);
                    } else {
                        entityF = entity;
                    }
                } else {
                    entityF = entity;
                }

                Workflow workflow = config.getWorkflowMap().get(entityF.getWorkflowId());
                entityF.setWorkflowState(workflow.getStateMap().get(transition.getToStateId()));
                entityF.setVisibleForRole(authorizationTool.findRolesWithReadPermission(entityF));
                entityF.setPublicExport(
                        config.getPoiCategoryMap().get(entityF.getPoiCategoryId()).isPublicExport() ? true :
                        workflow.getStateMap().get(entityF.getWorkflowStateId()).isPublicExport()
                );

                PoiEntity updated = updatePoi(entityF);

                ActivityEntity a = new ActivityEntity();
                a.setPoiId(updated.getId());
                a.setEventId(updated.getEventId());
	            a.setWorkflowId(workflow.getId());
	            a.setWorkflowStateId(updated.getWorkflowStateId());
                a.setType(ActivityEntity.ActivityType.WORKFLOW_TRANSITION);
                a.setComment(comment);
                a.setParams(new String[] { transition.getFromStateId(), transition.getToStateId() });
                activityService.log(a);

                // FIXME: prepsat na onBeforeCallBack
                if (transition.isForcesSingleAssignee() && (FLAG_DISABLE_FORCE_SINGLE_ASSIGN & flags) == 0) {
                    UserEntity loggedUser = appContext.getLoggedUser();
                    updated = assignUserExclusive(updated, loggedUser.getId());
                }

	            logger.info("Finished transition "+updated+" is in "+updated.getWorkflowStateId());

                return updated;
            }
        });

	    for (Long userId : updated.getUserIdList()) {
		    notificationService.sendPoiNotification(NotificationType.WORKFLOW_CHANGED, updated, userId);
	    }

	    return updated;
    }
}
