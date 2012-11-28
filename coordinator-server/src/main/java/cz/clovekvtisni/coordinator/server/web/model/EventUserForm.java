package cz.clovekvtisni.coordinator.server.web.model;

import com.googlecode.objectify.Key;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;

import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 28.11.12
 */
public class EventUserForm extends UserForm {

    @NotNull
    private Long eventId;

    private Long userInEventId;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getUserInEventId() {
        return userInEventId;
    }

    public void setUserInEventId(Long userInEventId) {
        this.userInEventId = userInEventId;
    }

    public UserInEventEntity buildUserInEventEntity() {
        UserInEventEntity inEventEntity = new UserInEventEntity();
        inEventEntity.setEventId(eventId);
        inEventEntity.setUserId(getId());
        inEventEntity.setParentKey(Key.create(UserEntity.class, getId()));

        return inEventEntity;
    }
}
