package cz.clovekvtisni.coordinator.server;

import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/28/12
 * Time: 12:02 AM
 */
public class AppContextTestImpl implements AppContext {

    private static final ThreadLocal<User> userEntityHolder = new ThreadLocal<User>();

    private static final ThreadLocal<Locale> localeHolder = new ThreadLocal<Locale>();

    @Override
    public void setLoggedUser(User loggedUser) {
        userEntityHolder.set(loggedUser);
    }

    @Override
    public User getLoggedUser() {
        return userEntityHolder.get();
    }

    @Override
    public Locale getLocale() {
        return localeHolder.get();
    }

    @Override
    public void setLocale(Locale locale) {
        localeHolder.set(locale);
    }
}
