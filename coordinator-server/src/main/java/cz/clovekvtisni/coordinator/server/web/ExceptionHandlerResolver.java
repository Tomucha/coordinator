package cz.clovekvtisni.coordinator.server.web;

import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.server.util.SignatureTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 12/21/11
 * Time: 12:36 PM
 */
public class ExceptionHandlerResolver extends DefaultHandlerExceptionResolver {

    private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        ModelAndView modelAndView = super.resolveException(request, response, handler, ex);
        if (modelAndView != null) {
            return modelAndView;
        }

        Map<String, Object> model = new HashMap<String, Object>();

        populateExceptionModel(request, ex, model);

        return new ModelAndView(getViewName(), model);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public static void populateExceptionModel(HttpServletRequest request, Throwable ex, Map<String, Object> model) {
        String exceptionMessage = null;
        String exceptionCause = null;
        String exceptionCauseMessage = null;
        String exceptionStack = null;
        String exceptionCode = "EXC:" + SignatureTool.md5Digest(new Date().toString() + Math.random()).substring(0, 10);


        Throwable rootCause = findRootCause(ex);
        if (rootCause != null) {
            StringWriter resultWriter = new StringWriter();
            rootCause.printStackTrace(new PrintWriter( resultWriter ) );
            exceptionStack = resultWriter.toString();
            exceptionCauseMessage = rootCause.getMessage();
            exceptionCause = rootCause.getClass().getName();
        }
        MaException maException = findMaException(ex);
        if (maException != null) {
            exceptionMessage = maException.getMessage();
            exceptionCode=exceptionCode+" "+maException.getCode();
        }
        logger.error(exceptionCode + " on " + request.getRequestURI(), ex);

        model.put("exceptionMessage", exceptionMessage);
        model.put("exceptionCause", exceptionCause);
        model.put("exceptionCauseMessage", exceptionCauseMessage);
        model.put("exceptionStack", exceptionStack);
        model.put("exceptionCode", exceptionCode);
    }

    protected String getViewName() {
        return "errors/common";
    }

    protected static Throwable findRootCause(Throwable e) {
   		if (e==null) return null;
   		if (e.getCause() == e) return e;
   		if (e.getCause() == null) return e;
   		return findRootCause(e.getCause());
   	}

   	protected static MaException findMaException(Throwable e) {
   		if (e == null) return null;
   		if (e instanceof MaException)
               return (MaException) e;
   		if (e == e.getCause()) return null;
   		return findMaException(e.getCause());
   	}
}
