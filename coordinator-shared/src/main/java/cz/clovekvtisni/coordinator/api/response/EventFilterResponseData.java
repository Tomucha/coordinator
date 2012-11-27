package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.UserInEvent;

import java.util.List;

public class EventFilterResponseData implements ApiResponseData {

    private Event[] events;

    private OrganizationInEvent[] organizationInEvents;

    private UserInEvent[] userInEvents;

    public EventFilterResponseData() {
    }

    public Event[] getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events.toArray(new Event[0]);
    }

    public void setEvents(Event[] users) {
        this.events = users;
    }

    public OrganizationInEvent[] getOrganizationInEvents() {
        return organizationInEvents;
    }

    public void setOrganizationInEvents(OrganizationInEvent[] organizationInEvents) {
        this.organizationInEvents = organizationInEvents;
    }

    public void setOrganizationInEvents(List<OrganizationInEvent> organizationInEvents) {
        this.organizationInEvents = organizationInEvents.toArray(new OrganizationInEvent[0]);
    }

    public UserInEvent[] getUserInEvents() {
        return userInEvents;
    }

    public void setUserInEvents(UserInEvent[] userInEvents) {
        this.userInEvents = userInEvents;
    }

    public void setUserInEvents(List<UserInEvent> userInEvents) {
        this.userInEvents = userInEvents.toArray(new UserInEvent[0]);
    }
}
