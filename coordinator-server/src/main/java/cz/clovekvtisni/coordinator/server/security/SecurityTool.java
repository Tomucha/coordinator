package cz.clovekvtisni.coordinator.server.security;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.security.command.PermissionCommand;
import cz.clovekvtisni.coordinator.server.security.permission.*;
import cz.clovekvtisni.coordinator.server.service.Service;
import cz.clovekvtisni.coordinator.server.util.MaAnnotationUtils;
import cz.clovekvtisni.coordinator.util.RunnableWithResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 5/25/11
 * Time: 9:17 PM
 */
@Component
public class SecurityTool implements BeanFactoryAware {

    private Logger log = LoggerFactory.getLogger(SecurityTool.class);

    private Map<PermissionCommandFactoryKey, PermissionCommand<CoordinatorEntity>> permissionCommandFactoryMap = new HashMap<PermissionCommandFactoryKey, PermissionCommand<CoordinatorEntity>>();

    private Map<String, Class<? extends Permission>> permissionsByKindMap = new HashMap<String, Class<? extends Permission>>();
    
    private Map<CommandRegistrarKey, String> commandRegistrarInfoMap = new HashMap<CommandRegistrarKey, String>();

    private ThreadLocal<Boolean> anonymousEnabledHolder = new ThreadLocal<Boolean>();

    private ThreadLocal<Boolean> securityDisabledHolder = new ThreadLocal<Boolean>();

    private BeanFactory beanFactory;

    @Autowired
    private AppContext appContext;

    private SpelExpressionParser parser = new SpelExpressionParser();

    private Map<ExpressionKey, Expression> checkPermissionExpressionCache = new ConcurrentHashMap<ExpressionKey, Expression>();

    @SuppressWarnings("unchecked")
	public <E extends CoordinatorEntity>  void registerPermissionCommand(Class<? extends E> entityClass, Class<? extends Permission> permissionClass, PermissionCommand<E> command,
                                                    String registrarInfo) {
        registerPermissionCommand(new PermissionCommandFactoryKey(entityClass, permissionClass), (PermissionCommand<CoordinatorEntity>) command, registrarInfo);
    }

    @SuppressWarnings("unchecked")
    public <E extends CoordinatorEntity> void registerPermissionCommand(String entityName, Class<? extends Permission> permissionClass, PermissionCommand<E> command, String registrarInfo) {
        registerPermissionCommand(new PermissionCommandFactoryKey(entityName, permissionClass), (PermissionCommand<CoordinatorEntity>) command, registrarInfo);
    }

    private synchronized void registerPermissionCommand(PermissionCommandFactoryKey key, PermissionCommand<CoordinatorEntity> command, String registrarInfo) {
        if (permissionCommandFactoryMap.containsKey(key)) {
            throw new IllegalStateException("permission command factory is already registered for " + key);
        }
        try {
            commandRegistrarInfoMap.put(new CommandRegistrarKey(command), registrarInfo);
            //Serializable[] emptyArgs = new Serializable[0];
            Class<? extends Permission> permissionClass = key.getPermissionClass();
            String permissionKind = permissionClass.getConstructor(String.class).newInstance("").getKind().toLowerCase();
            Class<? extends Permission> registeredPermission = permissionsByKindMap.get(permissionKind);
            if (registeredPermission != null && !registeredPermission.equals(permissionClass)) {
                throw new IllegalStateException("can't register permission " + permissionClass + ". Other one with same kind is already registered" + registeredPermission);
            }
            permissionsByKindMap.put(permissionKind, permissionClass);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        permissionCommandFactoryMap.put(key, command);
    }

    public boolean check(Permission permission) {
        if (permission.getEntity() == null && permission.getEntityKindName() == null) {
            log.info("permission denied because empty entity and name for {}", permission);
            return false;
        }

        List<PermissionCommandFactoryKey> keys = PermissionCommandFactoryKey.buildKeys(permission);
        for (PermissionCommandFactoryKey key : keys) {
            PermissionCommand<CoordinatorEntity> command = permissionCommandFactoryMap.get(key);
            if (command != null) {
                boolean permitted = command.isPermitted(permission.getEntity(), permission.getEntityKindName());
                if (!permitted) {
                    log.debug("permission denied for {} by command {} registered by {}", new Object[]{permission, command, commandRegistrarInfoMap.get(new CommandRegistrarKey(command))});
                }
                return permitted;
            }
        }
        log.warn("no permission command factory for " + permission);
        return false;
    }

    public Permission createPermission(String kind, CoordinatorEntity entity) {
        kind = kind.toLowerCase();
        Class<? extends Permission> permissionClass = permissionsByKindMap.get(kind);
        if (permissionClass == null) throw new IllegalStateException("no permission with kind " + kind + " is registered");
        try {
            return permissionClass.getConstructor(CoordinatorEntity.class).newInstance(entity);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Permission createPermission(String kind, String entityName) {
        kind = kind.toLowerCase();
        Class<? extends Permission> permissionClass = permissionsByKindMap.get(kind);
        if (permissionClass == null) throw new IllegalStateException("no permission with kind " + kind + " is registered");
        try {
            return permissionClass.getConstructor(entityName.getClass()).newInstance(entityName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void runWithAnonymousEnabled(Runnable runnable) {
        if (isAnonymousEnabled()) {
            runnable.run();
            return;
        }

        anonymousEnabledHolder.set(true);
        try {
            runnable.run();
        } finally {
            anonymousEnabledHolder.remove();
        }
    }

    public <R> R runWithAnonymousEnabled(RunnableWithResult<R> runnable) {
        if (isAnonymousEnabled()) {
            return runnable.run();
        }
        anonymousEnabledHolder.set(true);
        try {
            return runnable.run();
        } finally {
            anonymousEnabledHolder.remove();
        }
    }

    public <R> R runWithDisabledSecurity(RunnableWithResult<R> runnable) {
        if (isSecurityDisabled()) {
            return runnable.run();
        }
        securityDisabledHolder.set(true);
        try {
            return runnable.run();
        } finally {
            securityDisabledHolder.remove();
        }
    }

    public boolean isAnonymousEnabled() {
        return Boolean.TRUE.equals(anonymousEnabledHolder.get());
    }

    public boolean isSecurityDisabled() {
        return Boolean.TRUE.equals(securityDisabledHolder.get());
    }

    public Anonymous findAnonymousAnnotation(Method method) {
        MaAnnotationUtils.AnnotationHolder<Anonymous> annotationHolder = MaAnnotationUtils.findAnnotationExt(method, Anonymous.class);
        return annotationHolder == null ? null : annotationHolder.getAnnotation();
    }

    protected MaAnnotationUtils.AnnotationHolder<CheckPermission> findCheckPermissionAnnotation(Method method) {
        return MaAnnotationUtils.findAnnotationExt(method, CheckPermission.class);
    }

    public MaAnnotationUtils.AnnotationHolder<FilterResult> findFilterResultAnnotation(Method method) {
        return MaAnnotationUtils.findAnnotationExt(method, FilterResult.class);
    }

    public boolean canCallMethod(Object target, Method method, Object[] args) {

        MaAnnotationUtils.AnnotationHolder<CheckPermission> checkPermissionAnnotation = findCheckPermissionAnnotation(method);

        if (checkPermissionAnnotation != null && !isAnonymousEnabled()) {

            StandardEvaluationContext evaluationContext = buildEvaluationContext(target, checkPermissionAnnotation.getAnnotatedMethod(), args);

            ExpressionKey expressionKey = ExpressionKey.buildKey(method);

            Expression checkPermissionExpression = checkPermissionExpressionCache.get(expressionKey);
            if (checkPermissionExpression == null) {
                checkPermissionExpression = parser.parseExpression(checkPermissionAnnotation.getAnnotation().value());
                checkPermissionExpressionCache.put(expressionKey, checkPermissionExpression);
            }

            if (!Boolean.TRUE.equals(checkPermissionExpression.getValue(evaluationContext))) {
                return false;
            }
        }

        return true;
    }

    StandardEvaluationContext buildEvaluationContext(Object target, Method method, Object[] args) {
        StandardEvaluationContext evaluationContext;
        PermissionExpressionRootObject rootObject = new PermissionExpressionRootObject(method, args, target);
        evaluationContext = new LazyParamAwareEvaluationContext(rootObject, method, args);
        evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        preparePermissionExpressionEvaluationContext(evaluationContext);
        return evaluationContext;
    }

    protected void preparePermissionExpressionEvaluationContext(StandardEvaluationContext evaluationContext) {
        evaluationContext.setVariable("helper", buildHelper());
    }


    //TODO: umoznit jednoduchsie volanie (vytvaranie ServicePermissionCheckDescriptor) zo serveru

    public PermissionCheckResultModel checkServicePermissions(ServicePermissionCheckDescriptor[] descriptors) {
        final PermissionCheckResultModel resultModel = new PermissionCheckResultModel();
        for (ServicePermissionCheckDescriptor descriptor : descriptors) {
            try {
                Object service = beanFactory.getBean(Class.forName(descriptor.getServiceClassName()));
                if (service == null) {
                    log.warn("bean for class {} not found", descriptor.getServiceClassName());
                    continue;
                }
                if (!(service instanceof Service)) {
                    log.warn("bean {} doesn't implement ma.portal.shared.api.Service", descriptor.getServiceClassName());
                    continue;
                }
                try {
                    String[] parameterClassNames = descriptor.getParameterClassNames();
                    Class<?>[] parameterTypes = new Class<?>[parameterClassNames.length];
                    for (int i = parameterClassNames.length - 1; i >= 0; i--) {
                        parameterTypes[i] = ClassUtils.forName(parameterClassNames[i], getClass().getClassLoader());
                    }
                    Method method = service.getClass().getMethod(descriptor.getMethodName(), parameterTypes);
                    resultModel.addResult(descriptor.getDescriptorName(), canCallMethod(service, method, descriptor.getParameters()));
                } catch (NoSuchMethodException e) {
                    log.warn("method not found for descriptor " + descriptor, e);
                }
            } catch (ClassNotFoundException e) {
                log.warn("wong class " + descriptor.getServiceClassName(), descriptor.getServiceClassName());
            }
        }
        return resultModel;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private static class PermissionCommandFactoryKey {
        Class<? extends CoordinatorEntity> entityClass = null;
        String entityName = null;
        Class<? extends Permission> permissionClass;

        PermissionCommandFactoryKey(Class<? extends CoordinatorEntity> entityClass, Class<? extends Permission> permissionClass) {
            this.entityClass = entityClass;
            this.permissionClass = permissionClass;
        }

        PermissionCommandFactoryKey(String entityName, Class<? extends Permission> permissionClass) {
            this.entityName = entityName == null ? null : entityName.toLowerCase();
            this.permissionClass = permissionClass;
        }

        @SuppressWarnings("unused")
        PermissionCommandFactoryKey(Permission permission) {
            CoordinatorEntity entity = permission.getEntity();
            String entityName = permission.getEntityKindName();
            permissionClass = permission.getClass();
            if (entity == null) {
                this.entityName = entityName == null ? null : entityName.toLowerCase();
            }
            else if (entityName == null) {
                entityClass = entity.getClass();
            }
            else {
                throw new IllegalStateException("permission can't contain entity and entityName");
            }
        }

        @SuppressWarnings("unchecked")
        static List<PermissionCommandFactoryKey> buildKeys(Permission permission) {
            List<PermissionCommandFactoryKey> keys = new ArrayList<PermissionCommandFactoryKey>();
            CoordinatorEntity entity = permission.getEntity();
            String entityName = permission.getEntityKindName();
            Class<? extends Permission>  permissionClass = permission.getClass();

            if (entity == null) {
                keys.add(new PermissionCommandFactoryKey(entityName, permissionClass));
            }
            else {
                Class<?> entityClass = entity.getClass();
                while (entityClass != null && CoordinatorEntity.class.isAssignableFrom(entityClass)) {
                    keys.add(new PermissionCommandFactoryKey((Class<? extends CoordinatorEntity>) entityClass, permissionClass));
                    entityClass = entityClass.getSuperclass();
                }
            }
            return keys;
        }

        @SuppressWarnings("unused")
		public Class<? extends CoordinatorEntity> getEntityClass() {
            return entityClass;
        }

        @SuppressWarnings("unused")
		public String getEntityName() {
            return entityName;
        }

        public Class<? extends Permission> getPermissionClass() {
            return permissionClass;
        }

        @SuppressWarnings({"RedundantIfStatement"})
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PermissionCommandFactoryKey)) return false;

            PermissionCommandFactoryKey that = (PermissionCommandFactoryKey) o;

            if (entityClass != null ? !entityClass.equals(that.entityClass) : that.entityClass != null) return false;
            if (entityName != null ? !entityName.equals(that.entityName) : that.entityName != null) return false;
            if (permissionClass != null ? !permissionClass.equals(that.permissionClass) : that.permissionClass != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = entityClass != null ? entityClass.hashCode() : 0;
            result = 31 * result + (entityName != null ? entityName.hashCode() : 0);
            result = 31 * result + (permissionClass != null ? permissionClass.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "CommandFactoryKey{" +
                    "entityClass=" + entityClass +
                    ", entityName='" + entityName + '\'' +
                    ", permissionClass=" + permissionClass +
                    '}';
        }

    }

    private static class CommandRegistrarKey {
        private PermissionCommand<?> permissionCommand;

        private CommandRegistrarKey(PermissionCommand<?> permissionCommand) {
            this.permissionCommand = permissionCommand;
        }

        @SuppressWarnings("RedundantIfStatement")
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CommandRegistrarKey that = (CommandRegistrarKey) o;

            if (permissionCommand != null ? !permissionCommand.equals(that.permissionCommand) : that.permissionCommand != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return permissionCommand != null ? permissionCommand.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "CommandRegistrarKey{" +
                    "permissionCommand=" + permissionCommand +
                    '}';
        }


    }

    static class ExpressionKey {

        private String key;

        protected ExpressionKey(String key) {
            this.key = key;
        }

        public static ExpressionKey buildKey(Method method) {
            return new ExpressionKey(method.toGenericString());
        }

        @Override
        public String toString() {
            return key;
        }

        @SuppressWarnings("RedundantIfStatement")
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ExpressionKey that = (ExpressionKey) o;

            if (key != null ? !key.equals(that.key) : that.key != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return key != null ? key.hashCode() : 0;
        }

/*
        private String buildKey(Method method, String expression) {
       		StringBuilder sb = new StringBuilder();
       		sb.append(method.getDeclaringClass().getName());
       		sb.append("#");
       		sb.append(method.toString());
       		sb.append("#");
       		sb.append(expression);
       		return sb.toString();
       	}
*/

    }

    public SecurityHelper buildHelper() {
        return new SecurityHelper(this);
    }

    public static class SecurityHelper {
        SecurityTool securityTool;

        public SecurityHelper(SecurityTool securityTool) {
            this.securityTool = securityTool;
        }

        public boolean canCreate(String entityKindName) {
            return securityTool.check(new CreatePermission(entityKindName));
        }

        public boolean canUpdate(String entityKindName) {
            return securityTool.check(new UpdatePermission(entityKindName));
        }

        public boolean canDelete(String entityKindName) {
            return securityTool.check(new DeletePermission(entityKindName));
        }

        public boolean canRead(String entityKindName) {
            return securityTool.check(new ReadPermission(entityKindName));
        }

        public boolean canCreate(CoordinatorEntity<?> entity) {
            return securityTool.check(new CreatePermission(entity));
        }

        public boolean canUpdate(CoordinatorEntity<?> entity) {
            return securityTool.check(new UpdatePermission(entity));
        }

        public boolean canDelete(CoordinatorEntity<?> entity) {
            return securityTool.check(new DeletePermission(entity));
        }

        public boolean canRead(CoordinatorEntity<?> entity) {
            return securityTool.check(new ReadPermission(entity));
        }

        public boolean canDo(Permission permission) {
            return securityTool.check(permission);
        }
    }
}

