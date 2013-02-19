package cz.clovekvtisni.coordinator.api.request;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 23.11.12
 */
public class UserPushTokenRequestParams implements RequestParams {

    private String token;

    public UserPushTokenRequestParams(String token) {
        this.token = token;
    }

    public UserPushTokenRequestParams() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getSignature() {
        return String.valueOf(token);
    }

}
