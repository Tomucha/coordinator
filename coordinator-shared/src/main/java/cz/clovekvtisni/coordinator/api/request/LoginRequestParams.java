package cz.clovekvtisni.coordinator.api.request;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/27/12
 * Time: 12:25 AM
 */
public class LoginRequestParams implements RequestParams {

    private String login;

    private String password;

    public LoginRequestParams() {
    }

    public LoginRequestParams(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getSignature() {
        return login + "~" + password;
    }
}
