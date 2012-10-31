package cz.clovekvtisni.coordinator.server.security;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/27/12
 * Time: 4:41 PM
 */
public class AppContextSimpleBeanImpl implements AppContext {

    private UserEntity loggedUser;

    private Locale locale;

    @Override
    public void setLoggedUser(UserEntity loggedUser) {
        this.loggedUser = loggedUser;
    }

    @Override
    public UserEntity getLoggedUser() {
        return loggedUser;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public String toString() {
        return "AppContext{" +
                "loggedUser=" + loggedUser +
                ", locale=" + locale +
                '}';
    }
}
