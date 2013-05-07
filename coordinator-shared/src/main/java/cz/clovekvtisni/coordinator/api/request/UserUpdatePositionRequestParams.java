package cz.clovekvtisni.coordinator.api.request;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 11.12.12
 */
public class UserUpdatePositionRequestParams implements EventRequestParams {

    private Double latitude;

    private Double longitude;

    private Long eventId;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public String getSignature() {
        return longitude + "~" + latitude + "~" + eventId;
    }
}
