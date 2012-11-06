package cz.clovekvtisni.coordinator.server.security;

import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/28/12
 * Time: 12:03 AM
 */
public interface AppContext {
    void setLoggedUser(UserEntity loggedUser);

    UserEntity getLoggedUser();

    Locale getLocale();

    void setLocale(Locale locale);
}
