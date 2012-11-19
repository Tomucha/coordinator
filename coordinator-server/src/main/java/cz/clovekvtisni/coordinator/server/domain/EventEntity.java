package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.*;
import cz.clovekvtisni.coordinator.domain.Event;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Cache
@Entity(name = "Event")
public class EventEntity extends AbstractPersistentEntity<Event, EventEntity> {

    @Id
    private Long id;

    @Index
    @NotEmpty
    private String eventId;

    @NotEmpty
    private String name;

    private String description;

    @Ignore
    private EventLocationEntity[] eventLocationEntityList;

    public EventEntity() {
    }

    @Override
    public Key<EventEntity> getKey() {
        return Key.create(EventEntity.class, id);
    }

    @Override
    protected Event createTargetEntity() {
        return new Event();
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

    public void setEventId(String id) {
        this.eventId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventLocationEntity[] getEventLocationEntityList() {
        return eventLocationEntityList;
    }

    public void setEventLocationEntityList(EventLocationEntity[] eventLocationEntityList) {
        this.eventLocationEntityList = eventLocationEntityList;
    }

    public EventLocationEntity getFirstEventLocation() {
        if (eventLocationEntityList == null || eventLocationEntityList.length == 0) return null;
        return eventLocationEntityList[0];
    }

    @Override
    public String toString() {
        return "EventEntity{" +
                "id=" + id +
                ", eventId='" + eventId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
