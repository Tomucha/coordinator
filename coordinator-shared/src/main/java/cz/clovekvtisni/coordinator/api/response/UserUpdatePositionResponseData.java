package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.domain.UserInEvent;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 11.12.12
 */
public class UserUpdatePositionResponseData implements ApiResponseData {

    private UserInEvent updatedUserInEvent;

    public UserUpdatePositionResponseData() {
    }

    public UserUpdatePositionResponseData(UserInEvent updatedUserInEvent) {
        this.updatedUserInEvent = updatedUserInEvent;
    }

    public UserInEvent getUpdatedUserInEvent() {
        return updatedUserInEvent;
    }

    public void setUpdatedUserInEvent(UserInEvent updatedUserInEvent) {
        this.updatedUserInEvent = updatedUserInEvent;
    }
}
