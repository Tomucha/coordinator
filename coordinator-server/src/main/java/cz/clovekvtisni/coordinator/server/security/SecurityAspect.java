package cz.clovekvtisni.coordinator.server.security;


import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.util.MaAnnotationUtils;
import cz.clovekvtisni.coordinator.util.RunnableWithResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class SecurityAspect implements Ordered {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecurityTool securityTool;

    private ThreadLocal<Integer> serviceCallStack = new ThreadLocal<Integer>();

    private Map<SecurityTool.ExpressionKey, Expression> resultFilterExpressionCache = new ConcurrentHashMap<SecurityTool.ExpressionKey, Expression>();

    private SpelExpressionParser parser = new SpelExpressionParser();

    @Around("this(cz.clovekvtisni.coordinator.server.service.Service)")
    public Object checkPermissions(final ProceedingJoinPoint pjp) throws Throwable {
        if (serviceCallStack.get() == null) {
            serviceCallStack.set(0);
        }

        MethodSignature signature = (MethodSignature) pjp.getSignature();

        if (securityTool.isSecurityDisabled()) {
            return pjp.proceed();
        }

        Method method = signature.getMethod();
        Object[] args = pjp.getArgs();

        if (serviceCallStack.get() == 0 && !securityTool.canCallMethod(pjp.getTarget(), method, args)) {
            throw MaPermissionDeniedException.permissionDenied();
        }
        try {
            serviceCallStack.set(serviceCallStack.get()+1);
            //log.info("Call stack: "+serviceCallStack.get());
            Object toReturn;

            Anonymous anonymousAnnotation = securityTool.findAnonymousAnnotation(method);

            if (anonymousAnnotation != null && anonymousAnnotation.value() == Anonymous.Mode.PROPAGATE) {
                try {
                    toReturn = securityTool.runWithAnonymousEnabled(new RunnableWithResult<Object>() {
                        @Override
                        public Object run() {
                            try {
                                return pjp.proceed();
                            } catch (Throwable throwable) {
                                throw new MethodCallException(throwable);
                            }
                        }
                    });
                } catch (Exception e) {
                    throw e instanceof MethodCallException ? e.getCause() : e;
                }
            } else {
                toReturn = pjp.proceed();
            }

            MaAnnotationUtils.AnnotationHolder<FilterResult> filterResultAnnotation = securityTool.findFilterResultAnnotation(method);

            if (filterResultAnnotation != null && toReturn != null) {

                StandardEvaluationContext evaluationContext = securityTool.buildEvaluationContext(pjp.getTarget(), filterResultAnnotation.getAnnotatedMethod(), args);

                SecurityTool.ExpressionKey expressionKey = SecurityTool.ExpressionKey.buildKey(method);
                Expression expression = resultFilterExpressionCache.get(expressionKey);
                if (expression == null) {
                    expression = parser.parseExpression(filterResultAnnotation.getAnnotation().value());
                    resultFilterExpressionCache.put(expressionKey, expression);
                }

                if (toReturn instanceof CoordinatorEntity) {
                    if (!checkReturnEntityPermission((CoordinatorEntity<?>) toReturn, expression, evaluationContext)) {
                        toReturn = null;
                    }
                } else if (toReturn instanceof Iterable) {
                    Iterator iterator = ((Iterable) toReturn).iterator();
                    if (iterator != null) {
                        while (iterator.hasNext()) {
                            Object o = iterator.next();
                            if (o instanceof CoordinatorEntity) {
                                if (!checkReturnEntityPermission((CoordinatorEntity<?>) o, expression, evaluationContext)) {
                                    iterator.remove();
                                }
                            }
                        }
                    }
                }
            }

            return toReturn;
        } finally {
            //log.info("Call stack (dec): "+serviceCallStack.get());
            serviceCallStack.set(serviceCallStack.get()-1);
        }
    }

    protected boolean checkReturnEntityPermission(CoordinatorEntity<?> entity, Expression expression, EvaluationContext evaluationContext) {
        evaluationContext.setVariable("entity", entity);
        return Boolean.TRUE.equals(expression.getValue(evaluationContext));
    }

    @Override
    public int getOrder() {
        return -20;
    }


    private static class MethodCallException extends RuntimeException {

        private static final long serialVersionUID = 1660162169874248715L;

        private MethodCallException(Throwable cause) {
            super(cause);
        }
    }

}