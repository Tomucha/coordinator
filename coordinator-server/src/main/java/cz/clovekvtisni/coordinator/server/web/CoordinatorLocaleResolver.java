package cz.clovekvtisni.coordinator.server.web;

import org.springframework.web.servlet.i18n.AbstractLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 3:52 PM
 */
public class CoordinatorLocaleResolver extends AbstractLocaleResolver {
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        return Locale.getDefault();
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
    }
}
