package cz.clovekvtisni.coordinator.server.web;

import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.service.OrganizationInEventService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.web.filter.ApplicationInitFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 *
 * This interceptor checks EventPrerequisitiesRequired annotation on MVC Controllers,
 * in order to ensure UserInEvent and OrganizationInEvent records in database.
 *
 */
public class EventPrerequisitiesInterceptor extends HandlerInterceptorAdapter {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserInEventService userInEventService;

    @Autowired
    private OrganizationInEventService organizationInEventService;

    @Autowired
    private AppContext appContext;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            EventPrerequisitiesRequired required = AnnotationUtils.findAnnotation(method, EventPrerequisitiesRequired.class);
            if (required == null) {
                required = AnnotationUtils.findAnnotation(method.getDeclaringClass(), EventPrerequisitiesRequired.class);
            }

            if (required == null) {
                // annotation is not present
                return true;
            }

            logger.info("Event prerequisities are required, checking");

            if (appContext.getActiveOrganizationInEvent() == null) {
                response.sendRedirect("/admin/event/detail?eventId="+appContext.getActiveEvent().getId());
                return false;
            }
            if (appContext.getActiveUserInEvent() == null) {
                response.sendRedirect("/admin/event/user/edit?eventId="+appContext.getActiveEvent().getId()+"&userId="+appContext.getLoggedUser().getId());
                return false;
            }
            return true;
        }
        return true;
    }

}
