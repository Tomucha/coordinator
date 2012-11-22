package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.domain.User;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
public class RegisterResponseData implements ApiResponseData {

    private User user;

    private String authKey;

    public RegisterResponseData(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }
}
