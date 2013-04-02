package cz.clovekvtisni.coordinator.api.request;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 11.12.12
 */
public class EventPoiCreateRequestParams implements RequestParams {

    private long eventId;

    private double latitude;

    private double longitude;

    private String poiCategoryId;

    private String name;

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoiCategoryId() {
        return poiCategoryId;
    }

    public void setPoiCategoryId(String poiCategoryId) {
        this.poiCategoryId = poiCategoryId;
    }

    @Override
    public String getSignature() {
        return eventId + "~" + name + "~" + latitude + "~" + longitude;
    }
}
