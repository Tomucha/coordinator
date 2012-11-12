package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import cz.clovekvtisni.coordinator.domain.EventLocation;

import com.googlecode.objectify.annotation.Id;;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Cache
@Entity(name = "EventLocation")
public class EventLocationEntity extends AbstractPersistentEntity<EventLocation, EventLocationEntity> {

    @Id
    private Long id;

    @Index
    private String eventId;

    private Double latitude;

    private Double longitude;

    private Long radius;

    public EventLocationEntity() {
    }

    @Override
    protected EventLocation createTargetEntity() {
        return new EventLocation();
    }

    @Override
    public Key<EventLocationEntity> getKey() {
        return Key.create(EventLocationEntity.class, id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

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

    public Long getRadius() {
        return radius;
    }

    public void setRadius(Long radius) {
        this.radius = radius;
    }
}
