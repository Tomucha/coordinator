package cz.clovekvtisni.coordinator.server.aspect;

import cz.clovekvtisni.coordinator.server.validation.NoValidate;
import cz.clovekvtisni.coordinator.server.validation.Validate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.validator.engine.MethodConstraintViolationImpl;
import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodConstraintViolationException;
import org.hibernate.validator.method.MethodValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@Component
@Aspect
public class ValidationAspect implements Ordered {

    protected ValidatorFactory validatorFactory;

    @Autowired
    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

//    @Around("this(cz.clovekvtisni.coordinator.server.service.Service)")
    public Object validateMethodInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
        return doParameterValidation(joinPoint);
    }

    protected Object doParameterValidation(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Validator validator = getValidator(joinPoint);
        MethodValidator methodValidator = validator.unwrap(MethodValidator.class);

        Set<MethodConstraintViolation<Object>> violations = new HashSet<MethodConstraintViolation<Object>>();


        Method method = signature.getMethod();

        final NoValidate noValidate = method.getAnnotation(NoValidate.class);
        if (noValidate != null) {
            return joinPoint.proceed();
        }
        final Validate validate = method.getAnnotation(Validate.class);
        Class<?>[] groups = validate == null ? new Class[0] : validate.groups();

        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();
        int idx = -1;
        for (Annotation[] annotations : method.getParameterAnnotations()) {
            idx++;
            Object value = args[idx];
            if (annotations.length != 0) {
                Set<MethodConstraintViolation<Object>> paramViolations = methodValidator.validateParameter(joinPoint.getTarget(), method, value, idx, groups);
                if (!paramViolations.isEmpty()) {
                    violations.addAll(paramViolations);
                    continue;
                }
            }
            Class<?>[] paramGroups = groups;
            for (Annotation annotation : annotations) {
                if (annotation instanceof Validate) {
                    paramGroups = ((Validate) annotation).groups();
                    break;
                }
            }
            if (value != null) {
                for (ConstraintViolation<Object> violation : validator.validate(value, paramGroups)) {
                    MethodConstraintViolation<Object> methodConstraintViolation = new MethodConstraintViolationImpl<Object>(
                            violation.getMessageTemplate(),
                            violation.getMessage(),
                            method,
                            idx,
                            parameterNames == null ? "arg" + idx : parameterNames[idx],
                            violation.getRootBeanClass(),
                            violation.getRootBean(),
                            violation.getLeafBean(),
                            value,
                            violation.getPropertyPath(),
                            violation.getConstraintDescriptor(),
                            ElementType.PARAMETER
                    );
                    violations.add(methodConstraintViolation);
                }
            }
        }

        if (!violations.isEmpty()) {
            throw new MethodConstraintViolationException(violations);
        }

        Object result = joinPoint.proceed();

        violations = methodValidator.validateReturnValue(joinPoint.getTarget(), method, result, groups);

        if (!violations.isEmpty()) {
            throw new MethodConstraintViolationException(violations);
        }
        return result;
    }

    protected Validator getValidator(ProceedingJoinPoint joinPoint) {
        return validatorFactory.getValidator();
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
