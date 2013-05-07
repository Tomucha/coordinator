package cz.clovekvtisni.coordinator.api.request;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 11.12.12
 */
public class EventPoiTransitionRequestParams implements EventRequestParams {

    private long eventId;

    private long poiId;

    private String transitionId;

    @Override
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getPoiId() {
        return poiId;
    }

    public void setPoiId(long poiId) {
        this.poiId = poiId;
    }

    public String getTransitionId() {
        return transitionId;
    }

    public void setTransitionId(String transitionId) {
        this.transitionId = transitionId;
    }

    @Override
    public String getSignature() {
        return eventId + "~" + poiId + "~" + transitionId;
    }
}
