package cz.clovekvtisni.coordinator.api.request;

import cz.clovekvtisni.coordinator.domain.UserInEvent;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 11.12.12
 */
public class EventUserListRequestParams implements EventRequestParams {

    private Long eventId;

    private Date modifiedFrom;

    @Override
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Date getModifiedFrom() {
        return modifiedFrom;
    }

    public void setModifiedFrom(Date modifiedFrom) {
        this.modifiedFrom = modifiedFrom;
    }

    @Override
    public String getSignature() {
        return eventId + "~" + modifiedFrom;
    }
}
