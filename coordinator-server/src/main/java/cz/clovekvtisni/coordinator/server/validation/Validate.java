package cz.clovekvtisni.coordinator.server.validation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/27/11
 * Time: 11:34 PM
 */
@Target({ METHOD, PARAMETER })
@Retention(RUNTIME)
public @interface Validate {
    Class<?>[] groups() default { };
}
