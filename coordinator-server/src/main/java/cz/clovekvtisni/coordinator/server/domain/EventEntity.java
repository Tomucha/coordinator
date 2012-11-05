package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;
import cz.clovekvtisni.coordinator.domain.Event;

import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Unindexed
@Cached
@Entity(name = "Event")
public class EventEntity extends AbstractPersistentEntity<Event, EventEntity> {

    @Id
    private Long id;

    @Indexed
    private String eventId;

    private String name;

    private String description;

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

    @Override
    public String toString() {
        return "EventEntity{" +
                "id=" + id +
                ", eventId='" + eventId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
