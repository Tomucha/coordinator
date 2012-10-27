package cz.clovekvtisni.coordinator.server.validation.constraints;

import cz.clovekvtisni.coordinator.server.validation.constraints.impl.ComparePropertiesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 1/12/12
 * Time: 3:27 PM
 */
@Constraint(validatedBy = ComparePropertiesValidator.class)
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface CompareProperties {
    String message();

    Class<?>[] groups() default { };

   	Class<? extends Payload>[] payload() default { };
    
    String firstProperty();

    String secondProperty();

    Operator operator();

    enum Operator {
        LT, LE, EQ, NOT_EQ, GE, GT
    }

   	/**
   	 * Defines several {@code @NotEmpty} annotations on the same element.
   	 */
   	@Target({ TYPE, ANNOTATION_TYPE})
   	@Retention(RUNTIME)
   	@Documented
   	public @interface List {
   		CompareProperties[] value();
   	}
}
