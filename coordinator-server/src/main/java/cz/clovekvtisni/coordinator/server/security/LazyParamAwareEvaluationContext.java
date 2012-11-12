package cz.clovekvtisni.coordinator.server.security;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;

public class LazyParamAwareEvaluationContext extends StandardEvaluationContext {

    private final Method method;

    private final Object[] args;

    private boolean paramLoaded = false;


    public LazyParamAwareEvaluationContext(Object rootObject, Method method, Object[] args) {
        super(rootObject);

        this.method = method;
        this.args = args;
    }

    /**
     * Load the param information only when needed.
     */
    @Override
    public Object lookupVariable(String name) {
        Object variable = super.lookupVariable(name);
        if (variable != null) {
            return variable;
        }
        if (!this.paramLoaded) {
            loadArgsAsVariables();
            this.paramLoaded = true;
            variable = super.lookupVariable(name);
        }
        return variable;
    }

    private void loadArgsAsVariables() {
        // shortcut if no args need to be loaded
        if (ObjectUtils.isEmpty(this.args)) {
            return;
        }

/*
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations != null) {
            for (int i = parameterAnnotations.length - 1; i >= 0; i--) {
                Annotation[] annotations = parameterAnnotations[i];
                if (annotations != null) {
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof JsonRpcParam) {
                            String paramName = ((JsonRpcParam) annotation).value();
                            setVariable(paramName, this.args[i]);
                            break;
                        }
                    }
                }
            }
        }
*/

        // save arguments as indexed variables
        for (int i = 0; i < this.args.length; i++) {
            setVariable("p" + i, this.args[i]);
        }
    }
}