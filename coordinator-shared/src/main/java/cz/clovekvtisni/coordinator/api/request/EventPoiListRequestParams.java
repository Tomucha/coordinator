package cz.clovekvtisni.coordinator.api.request;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 11.12.12
 */
public class EventPoiListRequestParams implements RequestParams {

    private Long eventId;

    private Date modifiedFrom;

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
