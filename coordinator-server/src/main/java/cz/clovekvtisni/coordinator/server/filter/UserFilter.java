package cz.clovekvtisni.coordinator.server.filter;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:22 PM
 */
public class UserFilter extends AbstractFilter {

    private String login;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "UserFilter{" +
                "login='" + login + '\'' +
                '}';
    }
}
