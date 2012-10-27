package cz.clovekvtisni.coordinator.server.web.filter;


import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.service.SystemService;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.web.RequestKeys;
import cz.clovekvtisni.coordinator.server.web.SessionKeys;
import cz.clovekvtisni.coordinator.util.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/27/12
 * Time: 4:39 PM
 */
public class ApplicationInitFilter implements Filter {

    @Autowired
    private AppContext appContext;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private LocaleResolver localeResolver;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest hRequest = (HttpServletRequest) request;
        HttpServletResponse hResponse = (HttpServletResponse) response;

        appContext.setLocale(localeResolver.resolveLocale(hRequest));

        Long loggedUserId = findLoggedUserId(hRequest);

        String root = hRequest.getContextPath();
        hRequest.setAttribute(RequestKeys.ROOT, root);

        try {
            if (loggedUserId == null && !isWithoutLoginRequest(hRequest)) {
                hResponse.sendRedirect(root + "/login?retUrl=" + Url.encode(hRequest.getRequestURL().toString()));
                return;
            }

            chain.doFilter(request, response);
        } finally {
            if (appContext.getLoggedUser() == null || appContext.getLoggedUser().getId() == null) {
                HttpSession session = hRequest.getSession(false);
                if (session != null) {
                    session.removeAttribute(SessionKeys.LOGGED_USER_ID);
                }
            }
            else if (!appContext.getLoggedUser().getId().equals(loggedUserId)) {
                hRequest.getSession().setAttribute(SessionKeys.LOGGED_USER_ID, appContext.getLoggedUser().getId());
            }
            appContext.setLocale(null);
        }
    }

    protected Long findLoggedUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        Long loggedUserId = null;

        if (session != null) {
            loggedUserId = (Long) session.getAttribute(SessionKeys.LOGGED_USER_ID);
            if (loggedUserId != null) {
                UserEntity userEntity = userService.findById(loggedUserId);
                if (userEntity == null) {
                    session.removeAttribute(SessionKeys.LOGGED_USER_ID);
                    loggedUserId = null;
                }
                else {
                    appContext.setLoggedUser(userEntity);
                }
            }
        }

        return loggedUserId;
    }

    private String getNormalizedUri(HttpServletRequest hr) {
        String uri = (String) hr.getAttribute(RequestKeys.NORMALIZED_URI);
        if (uri == null) {
            uri = hr.getRequestURI();
            if (!"".equals(hr.getContextPath()) && uri.startsWith(hr.getContextPath())) {
                uri = uri.equals(hr.getContextPath()) ? "/" : uri.substring(hr.getContextPath().length());
            }
            hr.setAttribute(RequestKeys.NORMALIZED_URI, uri);
        }
        return uri;
    }

    private Pattern withoutLoginPattern = Pattern.compile("^/(?:login|logout|api|_ah)(?:/|$)");

    private boolean isWithoutLoginRequest(HttpServletRequest hr) {
        return isUriMatch(hr, withoutLoginPattern);
    }

    private boolean isUriStartsWith(HttpServletRequest hr, String prefix) {
        final String uriWithoutContext = getNormalizedUri(hr);
        return uriWithoutContext.startsWith(prefix);
    }

    private boolean isUriMatch(HttpServletRequest hr, Pattern pattern) {
        final String uriWithoutContext = getNormalizedUri(hr);
        return pattern.matcher(uriWithoutContext).find();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        systemService.initApplication();
    }

    @Override
    public void destroy() {
    }
}
