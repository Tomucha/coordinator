package cz.clovekvtisni.coordinator.server.web.model;

import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.util.ValueTool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 28.11.12
 */
public class EventFilterParams {

    private Long eventId;

    private String userFulltext;

    private Long groupId;

    private String retUrl;

    private String workflowId;

    private String workflowStateId;

    public EventFilterParams() {
    }

    public EventFilterParams(EventEntity event) {
        eventId = event != null ? event.getId() : null;
    }

    public EventFilterParams(Long eventId) {
        this.eventId = eventId;
    }

    public String getUserFulltext() {
        return userFulltext;
    }

    public void setUserFulltext(String userFulltext) {
        this.userFulltext = userFulltext;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /** @deprecated */
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(2);
        if (eventId != null)
            map.put("eventId", eventId.toString());
        if (userFulltext != null)
            map.put("userFulltext", userFulltext);

        return map;
    }

    public String getRetUrl() {
        return retUrl;
    }

    public void setRetUrl(String retUrl) {
        this.retUrl = retUrl;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkflowStateId() {
        return workflowStateId;
    }

    public void setWorkflowStateId(String workflowStateId) {
        this.workflowStateId = workflowStateId;
    }

    public PoiFilter populatePoiFilter(PoiFilter filter) {
        filter.setEventIdVal(getEventId());
        if (!ValueTool.isEmpty(getWorkflowId())) {
            filter.setWorkflowIdVal(getWorkflowId());
        }
        if (!ValueTool.isEmpty(getWorkflowStateId())) {
            filter.setWorkflowStateIdVal(getWorkflowStateId());
        }

        return filter;
    }

    public UserInEventFilter populateUserInEventFilter(UserInEventFilter filter) {
        filter.setEventIdVal(getEventId());
        if (!ValueTool.isEmpty(getUserFulltext())) {
            final String fullText = getUserFulltext().trim().toLowerCase();
            filter.addAfterLoadCallback(new Filter.AfterLoadCallback<UserInEventEntity>() {
                @Override
                public boolean accept(UserInEventEntity entity) {
                    UserEntity user = entity.getUserEntity();
                    return user != null && user.getFullName() != null && user.getFullName().toLowerCase().contains(fullText);
                }
            });
        }
        final Long groupId = getGroupId();
        if (groupId != null) {
            filter.addAfterLoadCallback(new Filter.AfterLoadCallback<UserInEventEntity>() {
                @Override
                public boolean accept(UserInEventEntity entity) {
                    return entity.getGroupIdList() != null &&
                            Arrays.asList(entity.getGroupIdList()).contains(groupId);
                }
            });
        }

        return filter;
    }
}
