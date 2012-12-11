package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.domain.User;

public class LoginResponseData implements ApiResponseData {

    private User user;

    private String authKey;

    public LoginResponseData(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    @Override
	public String toString() {
		return "LoginUserResponse [user=" + user + "]";
	}
}
