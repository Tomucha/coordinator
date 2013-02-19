package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.*;
import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.domain.EventLocation;
import cz.clovekvtisni.coordinator.server.util.EntityTool;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Arrays;

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
    private String eventKey;

    @NotEmpty
    private String name;

    private String description;

    @Ignore
    private EventLocationEntity[] eventLocationEntityList;

    public EventEntity() {
    }

    @Override
    @JsonIgnore
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

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String id) {
        this.eventKey = id;
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
    public Event buildTargetEntity() {
        Event event = super.buildTargetEntity();

        if (eventLocationEntityList != null) {
            event.setLocationList(
                    new EntityTool().buildTargetEntities(Arrays.asList(eventLocationEntityList))
                            .toArray(new EventLocation[0])
            );
        }

        return event;
    }

    @Override
    public String toString() {
        return "EventEntity{" +
                "id=" + id +
                ", eventKey='" + eventKey + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
