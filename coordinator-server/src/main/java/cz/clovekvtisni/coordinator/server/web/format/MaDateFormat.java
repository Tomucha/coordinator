package cz.clovekvtisni.coordinator.server.web.format;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 12/20/11
 * Time: 10:02 AM
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaDateFormat {
    
    Style style() default Style.NONE;
    
    String pattern() default "";
    
    public enum Style {
        
        DATE,

        TIME,

        DATE_TIME,

        NONE
    }
}
