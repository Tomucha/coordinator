package cz.clovekvtisni.coordinator.server.web;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created with IntelliJ IDEA.
 * User: tomucha
 * Date: 16.04.13
 * Time: 14:00
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface EventPrerequisitiesRequired {

    // controler requires UserInEvent and OrganizationInEvent to be present

}
