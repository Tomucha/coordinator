package cz.clovekvtisni.coordinator.server.web.model;

import cz.clovekvtisni.coordinator.server.domain.EventEntity;

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
}
