package cz.clovekvtisni.coordinator.server.validation;

import org.hibernate.validator.engine.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 6/12/12
 * Time: 5:38 PM
 */
public class ValidatorTool {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public void setPropertyPathIndex(ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder, Integer index) {
        try {
            Field field = violationBuilder.getClass().getDeclaredField("propertyPath");
            field.setAccessible(true);
            Object path = field.get(violationBuilder);
            if (path instanceof PathImpl) {
                PathImpl pathImpl = (PathImpl) path;
                pathImpl.setLeafNodeIndex(index);
            }
        } catch (Exception e) {
            logger.warn("can't hack propertyPath creation - fallback to wrong propertyPath");
            violationBuilder.addNode("[" + index + "]");
        }
    }

    public void reportViolations(ConstraintValidatorContext context, Integer propertyIndex, Set<ConstraintViolation<?>> violations) {
        context.disableDefaultConstraintViolation();
        for (ConstraintViolation v : violations) {
            ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(v.getMessageTemplate());
            if (propertyIndex != null) {
                setPropertyPathIndex(violationBuilder, propertyIndex);
            }
            Path path = v.getPropertyPath();
            for (Path.Node node : path) {
                violationBuilder.addNode(node.getName());
            }
            violationBuilder.addConstraintViolation();
        }
    }
}
