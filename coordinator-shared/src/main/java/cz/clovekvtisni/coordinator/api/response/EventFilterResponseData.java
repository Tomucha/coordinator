package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.domain.User;

import java.util.List;

public class EventFilterResponseData implements ApiResponseData {

    private Event[] events;

    public EventFilterResponseData() {
    }

    public EventFilterResponseData(List<Event> events) {
        setEvents(events);
    }

    public EventFilterResponseData(Event... events) {
        this.events = events;
    }

    public Event[] getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events.toArray(new Event[0]);
    }

    public void setUsers(Event[] users) {
        this.events = users;
    }
}
