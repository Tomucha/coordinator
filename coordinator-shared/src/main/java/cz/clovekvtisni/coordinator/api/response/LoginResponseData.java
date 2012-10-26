package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.domain.User;

public class LoginResponseData implements ApiResponseData {

    private User user;

    public LoginResponseData(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

	@Override
	public String toString() {
		return "LoginUserResponse [user=" + user + "]";
	}
}
