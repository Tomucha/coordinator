package cz.clovekvtisni.coordinator.server.web.model;

import cz.clovekvtisni.coordinator.domain.EventLocation;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 8.11.12
 */
public class EventForm extends EventEntity {

    private List<EventLocation> locationList;

    public List<EventLocation> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<EventLocation> locationList) {
        this.locationList = locationList;
    }
}
