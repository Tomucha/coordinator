package cz.clovekvtisni.coordinator.api.request;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/27/12
 * Time: 12:25 AM
 */
public class LoginRequestParams implements RequestParams {

    private String email;

    private String password;

    public LoginRequestParams() {
    }

    public LoginRequestParams(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getSignature() {
        return email + "~" + password;
    }
}
