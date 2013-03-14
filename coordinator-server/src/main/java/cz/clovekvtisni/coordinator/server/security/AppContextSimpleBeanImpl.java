package cz.clovekvtisni.coordinator.server.security;

import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/27/12
 * Time: 4:41 PM
 */
public class AppContextSimpleBeanImpl implements AppContext {

    private UserEntity loggedUser;

    /** If event is requested in URL, activeEvent is set */
    private EventEntity activeEvent;

    /** If event is requested in URL, activeOrganizationInEvent for loggetUser is set */
    private OrganizationInEventEntity activeOrganizationInEvent;

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

    public EventEntity getActiveEvent() {
        return activeEvent;
    }

    public void setActiveEvent(EventEntity activeEvent) {
        this.activeEvent = activeEvent;
    }

    @Override
    public OrganizationInEventEntity getActiveOrganizationInEvent() {
        return activeOrganizationInEvent;
    }

    @Override
    public void setActiveOrganizationInEvent(OrganizationInEventEntity organizationInEvent) {
        this.activeOrganizationInEvent = organizationInEvent;
    }

    @Override
    public String toString() {
        return "AppContext{" +
                "loggedUser=" + loggedUser +
                ", locale=" + locale +
                '}';
    }
}
