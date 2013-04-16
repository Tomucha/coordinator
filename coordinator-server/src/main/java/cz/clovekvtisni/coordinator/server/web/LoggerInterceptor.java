package cz.clovekvtisni.coordinator.server.web;

import cz.clovekvtisni.coordinator.server.security.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: tomucha
 * Date: 16.04.13
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
public class LoggerInterceptor extends HandlerInterceptorAdapter {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppContext appContext;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("Calling: "+request.getRequestURI()+" with user="+appContext.getLoggedUser()+" on event="+appContext.getActiveEvent());
        return true;
    }

}
