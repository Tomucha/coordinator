package cz.clovekvtisni.coordinator.domain;

public class Event extends AbstractModifiableEntity {

    private String name;

    private String description;

    private EventLocation[] locationList;

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

    public EventLocation[] getLocationList() {
        return locationList;
    }

    public void setLocationList(EventLocation[] locationList) {
        this.locationList = locationList;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
