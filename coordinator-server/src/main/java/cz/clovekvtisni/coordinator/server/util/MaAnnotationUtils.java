package cz.clovekvtisni.coordinator.server.util;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class MaAnnotationUtils extends AnnotationUtils {

    private static final Map<Class<?>, Boolean> annotatedInterfaceCache;

    static {
        Map<Class<?>, Boolean> cache;
        try {
            Field field = AnnotationUtils.class.getDeclaredField("annotatedInterfaceCache");
            field.setAccessible(true);
            //noinspection unchecked
            cache = (Map<Class<?>, Boolean>) field.get(null);
        } catch (Throwable e) {
            cache = new WeakHashMap<Class<?>, Boolean>();
        }
        annotatedInterfaceCache = cache;
    }

    public static <A extends Annotation> AnnotationHolder<A> getAnnotationExt(Method method, Class<A> annotationType) {
   		Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
   		A ann = resolvedMethod.getAnnotation(annotationType);
   		if (ann == null) {
   			for (Annotation metaAnn : resolvedMethod.getAnnotations()) {
   				ann = metaAnn.annotationType().getAnnotation(annotationType);
   				if (ann != null) {
   					break;
   				}
   			}
   		}
        return ann == null ? null : new AnnotationHolder<A>(ann, resolvedMethod);
   	}

    public static <A extends Annotation> AnnotationHolder<A> findAnnotationExt(Method method, Class<A> annotationType) {
        AnnotationHolder<A> toReturn = getAnnotationExt(method, annotationType);
        Class<?> cl = method.getDeclaringClass();
        if (toReturn == null) {
            toReturn = searchOnInterfacesExt(method, annotationType, cl.getInterfaces());
        }
        while (toReturn == null) {
            cl = cl.getSuperclass();
            if (cl == null || cl == Object.class) {
                break;
            }
            try {
                Method equivalentMethod = cl.getDeclaredMethod(method.getName(), method.getParameterTypes());
                toReturn = getAnnotationExt(equivalentMethod, annotationType);
                if (toReturn == null) {
                    toReturn = searchOnInterfacesExt(method, annotationType, cl.getInterfaces());
                }
            } catch (NoSuchMethodException ex) {
                // We're done...
            }
        }
        return toReturn;
    }

    private static <A extends Annotation> AnnotationHolder<A> searchOnInterfacesExt(Method method, Class<A> annotationType, Class<?>[] ifcs) {
        AnnotationHolder<A> toReturn = null;
        for (Class<?> iface : ifcs) {
            if (isInterfaceWithAnnotatedMethods(iface)) {
                Method equivalentMethod = findMethod(iface, method.getName(), method.getParameterTypes());
                if (equivalentMethod != null) {
                    A annotation = getAnnotation(equivalentMethod, annotationType);
                    if (annotation != null) {
                        return new AnnotationHolder<A>(annotation, equivalentMethod);
                    }
                }
            }
        }
        for (Class<?> iface : ifcs) {
            Class<?>[] interfaces = iface.getInterfaces();
            if (interfaces.length > 0) {
                toReturn = searchOnInterfacesExt(method, annotationType, interfaces);
                if (toReturn != null) {
                    break;
                }
            }
        }
        return toReturn;
    }

    private static Method findMethod(Class<?> iface, String name, Class<?>[] parameterTypes) {
        //TODO caching
        try {
            return iface.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            for (Method method : iface.getMethods()) {
                if (method.getName().equals(name)) {
                    if (isOverrided(parameterTypes, method.getParameterTypes())) {
                        return method;
                    }
                }
            }
            return null;
        }
    }

    private static boolean isOverrided(Class<?>[] parameter, Class<?>[] superParameter) {
        int length = parameter.length;
        if (superParameter.length != length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (!superParameter[i].isAssignableFrom(parameter[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isInterfaceWithAnnotatedMethods(Class<?> iface) {
        synchronized (annotatedInterfaceCache) {
            Boolean flag = annotatedInterfaceCache.get(iface);
            if (flag != null) {
                return flag;
            }
            boolean found = false;
            for (Method ifcMethod : iface.getMethods()) {
                if (ifcMethod.getAnnotations().length > 0) {
                    found = true;
                    break;
                }
            }
            annotatedInterfaceCache.put(iface, found);
            return found;
        }
    }

    public static class AnnotationHolder<A extends Annotation> {
        private A annotation;
        private Method annotatedMethod;
        private Class<?> annotatedClass;

        public AnnotationHolder(A annotation, Method annotatedMethod) {
            this.annotation = annotation;
            this.annotatedMethod = annotatedMethod;
        }

        public AnnotationHolder(A annotation, Class<?> annotatedClass) {
            this.annotation = annotation;
            this.annotatedClass = annotatedClass;
        }

        public A getAnnotation() {
            return annotation;
        }

        public void setAnnotation(A annotation) {
            this.annotation = annotation;
        }

        public Method getAnnotatedMethod() {
            return annotatedMethod;
        }

        public void setAnnotatedMethod(Method annotatedMethod) {
            this.annotatedMethod = annotatedMethod;
        }

        public Class<?> getAnnotatedClass() {
            return annotatedClass;
        }

        public void setAnnotatedClass(Class<?> annotatedClass) {
            this.annotatedClass = annotatedClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            AnnotationHolder that = (AnnotationHolder) o;

            if (annotatedClass != null ? !annotatedClass.equals(that.annotatedClass) : that.annotatedClass != null) {
                return false;
            }
            if (annotatedMethod != null ? !annotatedMethod.equals(that.annotatedMethod) : that.annotatedMethod != null) {
                return false;
            }
            if (annotation != null ? !annotation.equals(that.annotation) : that.annotation != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = annotation != null ? annotation.hashCode() : 0;
            result = 31 * result + (annotatedMethod != null ? annotatedMethod.hashCode() : 0);
            result = 31 * result + (annotatedClass != null ? annotatedClass.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "AnnotationHolder{" +
                    "annotation=" + annotation +
                    ", annotatedMethod=" + annotatedMethod +
                    ", annotatedClass=" + annotatedClass +
                    '}';
        }
    }
}
