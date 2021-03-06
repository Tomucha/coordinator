package cz.clovekvtisni.coordinator.server;

import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/28/12
 * Time: 12:02 AM
 */
public class AppContextTestImpl implements AppContext {

    private static final ThreadLocal<UserEntity> userEntityHolder = new ThreadLocal<UserEntity>();

    private static final ThreadLocal<Locale> localeHolder = new ThreadLocal<Locale>();

    @Override
    public void setLoggedUser(UserEntity loggedUser) {
        userEntityHolder.set(loggedUser);
    }

    @Override
    public UserEntity getLoggedUser() {
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

    @Override
    public EventEntity getActiveEvent() {
        throw new IllegalStateException("NYI");
    }

    @Override
    public void setActiveEvent(EventEntity event) {
        throw new IllegalStateException("NYI");
    }

    @Override
    public OrganizationInEventEntity getActiveOrganizationInEvent() {
        return null;  // TODO
    }

    @Override
    public void setActiveOrganizationInEvent(OrganizationInEventEntity organizationInEvent) {
        // TODO
    }

    @Override
    public UserInEventEntity getActiveUserInEvent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setActiveUserInEvent(UserInEventEntity activeUserInEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSystemCall() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setSystemCall(boolean systemCall) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
