package cz.clovekvtisni.coordinator.server.security;

import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;

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

    EventEntity getActiveEvent();

    void setActiveEvent(EventEntity event);

    OrganizationInEventEntity getActiveOrganizationInEvent();

    void setActiveOrganizationInEvent(OrganizationInEventEntity organizationInEvent);

    UserInEventEntity getActiveUserInEvent();

    void setActiveUserInEvent(UserInEventEntity activeUserInEvent);

    boolean isSystemCall();

    void setSystemCall(boolean systemCall);


}
