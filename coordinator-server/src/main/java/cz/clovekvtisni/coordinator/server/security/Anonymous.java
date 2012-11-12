package cz.clovekvtisni.coordinator.server.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: Nov 22, 2010
 * Time: 1:34:40 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.TYPE})
public @interface Anonymous {

    Mode value() default Mode.ENABLE;

    public static enum Mode {
        DISABLE, ENABLE, PROPAGATE
    }
}
