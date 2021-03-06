package cz.clovekvtisni.coordinator.server.web.model;

import com.googlecode.objectify.Key;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.util.ValueTool;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 28.11.12
 */
public class EventUserForm extends UserForm {

    @NotNull
    private Long eventId;

    private Long userId;

    private List<Long> groupIdList;

    private Double lastLocationLatitude;

    private Double lastLocationLongitude;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public UserInEventEntity buildUserInEventEntity() {
        UserInEventEntity inEventEntity = new UserInEventEntity();
        inEventEntity.setLastLocationLatitude(getLastLocationLatitude());
        inEventEntity.setLastLocationLongitude(getLastLocationLongitude());
        inEventEntity.setEventId(eventId);
        Long userId = getUserId();
        if (userId != null) {
            inEventEntity.setUserId(userId);
            inEventEntity.setParentKey(Key.create(UserEntity.class, userId));
        }
        if (groupIdList != null)
            inEventEntity.setGroupIdList(groupIdList.toArray(new Long[0]));

        return inEventEntity;
    }

    public List<Long> getGroupIdList() {
        return groupIdList;
    }

    public void setGroupIdList(List<Long> groupIdList) {
        this.groupIdList = groupIdList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getLastLocationLatitude() {
        return lastLocationLatitude;
    }

    public void setLastLocationLatitude(Double lastLocationLatitude) {
        this.lastLocationLatitude = lastLocationLatitude;
    }

    public Double getLastLocationLongitude() {
        return lastLocationLongitude;
    }

    public void setLastLocationLongitude(Double lastLocationLongitude) {
        this.lastLocationLongitude = lastLocationLongitude;
    }

    public void postValidate(BindingResult bindingResult, MessageSource messageSource, Locale locale) {
        if (getUserId() == null && ValueTool.isEmpty(getPassword())) {
            bindingResult.addError(new FieldError("form", "password", getPassword(), false, null, null, messageSource.getMessage("javax.validation.constraints.NotNull.message", null, locale)));

        } else if (getUserId() == null && getPassword() != null && !getPassword().equals(getConfirmPassword())) {
            bindingResult.addError(new FieldError("form", "confirmPassword", getPassword(), false, null, null, messageSource.getMessage("error.PASSWORD_CONFIRM_FAILED", null, locale)));
        }
    }
}
