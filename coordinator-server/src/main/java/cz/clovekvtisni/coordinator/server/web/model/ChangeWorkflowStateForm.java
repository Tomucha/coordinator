package cz.clovekvtisni.coordinator.server.web.model;

import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 10.1.13
 */
public class ChangeWorkflowStateForm {

    @NotNull
    private Long eventId;

    @NotNull
    private Long placeId;

    private String transitionId;

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getTransitionId() {
        return transitionId;
    }

    public void setTransitionId(String transitionId) {
        this.transitionId = transitionId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
