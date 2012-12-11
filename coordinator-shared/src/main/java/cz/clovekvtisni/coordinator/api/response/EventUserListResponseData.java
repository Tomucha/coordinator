package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.domain.UserInEvent;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 11.12.12
 */
public class EventUserListResponseData implements ApiResponseData {

    private UserInEvent[] userInEvents;

    public EventUserListResponseData() {
    }

    public EventUserListResponseData(List<UserInEvent> userInEvents) {
        this.userInEvents = userInEvents.toArray(new UserInEvent[0]);
    }

    public EventUserListResponseData(UserInEvent[] userInEvents) {
        this.userInEvents = userInEvents;
    }

    public UserInEvent[] getUserInEvents() {
        return userInEvents;
    }

    public void setUserInEvents(UserInEvent[] userInEvents) {
        this.userInEvents = userInEvents;
    }
}
