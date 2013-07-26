package cz.clovekvtisni.coordinator.server.web.filter;


import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.SecurityTool;
import cz.clovekvtisni.coordinator.server.service.*;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.RequestKeys;
import cz.clovekvtisni.coordinator.server.web.SessionKeys;
import cz.clovekvtisni.coordinator.util.RunnableWithResult;
import cz.clovekvtisni.coordinator.util.Url;
import cz.clovekvtisni.coordinator.util.ValueTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * scope=request, see applicationContext.xml
     * <bean id="appContext" class="cz.clovekvtisni.coordinator.server.security.AppContextSimpleBeanImpl" scope="request">
     */
    @Autowired
    private AppContext appContext;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private OrganizationInEventService organizationInEventService;

    @Autowired
    private UserInEventService userInEventService;

    @Autowired
    private SecurityTool securityTool;

    private static boolean appInitialized = false;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest hRequest = (HttpServletRequest) request;
        HttpServletResponse hResponse = (HttpServletResponse) response;

        appContext.setLocale(localeResolver.resolveLocale(hRequest));

        synchronized (this.getClass()) {
            if (!appInitialized) {
                systemService.initApplication();
                appInitialized = true;
            }
        }

        Long loggedUserId = findLoggedUserId(hRequest);

        String root = hRequest.getContextPath();
        hRequest.setAttribute(RequestKeys.ROOT, root);
        String pathUri = getNormalizedUri(hRequest);

        if (!isApiCall(hRequest)) {
            String eventId = hRequest.getParameter("eventId");
            if (!ValueTool.isEmpty(eventId)) {
                // we have an active event in URL
                final Long eventIdNum = Long.parseLong(eventId);
                EventEntity e = securityTool.runWithDisabledSecurity(new RunnableWithResult<EventEntity>() {
                    @Override
                    public EventEntity run() {
                        return eventService.findById(eventIdNum, EventService.FLAG_FETCH_LOCATIONS);
                    }
                });
                appContext.setActiveEvent(e);
            }
        }

        try {
            if (loggedUserId == null && !isWithoutLoginRequest(hRequest)) {
                redirectWithBacklink("/login", hRequest, hResponse);
                return;
            }

            // FIXME: kde se bere logged user? je v tom bordel, predelat do interceptoru rikam ja
            UserEntity loggedUser = appContext.getLoggedUser();
            EventEntity activeEvent = appContext.getActiveEvent();
            OrganizationInEventEntity organizationInEventEntity = null;
            UserInEventEntity userInEventEntity = null;

            if (loggedUser != null && activeEvent != null) {
                organizationInEventEntity = organizationInEventService.findEventInOrganization(activeEvent.getId(), loggedUser.getOrganizationId(), 0l);
                final Long fActiveEventId = activeEvent.getId();
                final Long fLoggedUserId = loggedUser.getId();
                userInEventEntity = securityTool.runWithDisabledSecurity(new RunnableWithResult<UserInEventEntity>() {
                    @Override
                    public UserInEventEntity run() {
                        return userInEventService.findById(fActiveEventId, fLoggedUserId, UserInEventService.FLAG_FETCH_GROUPS);
                    }
                });
            }

            appContext.setActiveOrganizationInEvent(organizationInEventEntity);
            appContext.setActiveUserInEvent(userInEventEntity);

            chain.doFilter(request, response);
        } catch (Throwable e) {
            logger.error("Error in request: "+e, e);
            throw new IllegalStateException(e);
        } finally {
            if (appContext.getLoggedUser() == null || appContext.getLoggedUser().getId() == null) {
                HttpSession session = hRequest.getSession(false);
                if (session != null) {
                    session.removeAttribute(SessionKeys.LOGGED_USER_ID);
                }
            }
            else if (!appContext.getLoggedUser().getId().equals(loggedUserId)) {
                Long userId = appContext.getLoggedUser().getId();
                hRequest.getSession().setAttribute(SessionKeys.LOGGED_USER_ID, userId);
            }
            appContext.setLocale(null);
        }
    }

    public static boolean isApiCall(HttpServletRequest hRequest) {
        return isUriStartsWith(hRequest,  "/api");
    }

    protected Long findLoggedUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            final Long loggedUserId = (Long) session.getAttribute(SessionKeys.LOGGED_USER_ID);
            if (loggedUserId != null) {
                UserEntity userEntity = securityTool.runWithDisabledSecurity(new RunnableWithResult<UserEntity>() {
                    @Override
                    public UserEntity run() {
                        return userService.findById(loggedUserId, UserService.FLAG_FETCH_SKILLS | UserService.FLAG_FETCH_EQUIPMENT);
                    }
                });
                if (userEntity == null) {
                    session.removeAttribute(SessionKeys.LOGGED_USER_ID);
                    return null;
                }
                else {
                    appContext.setLoggedUser(userEntity);
                    return userEntity.getId();
                }
            }
        }

        return null;
    }

    public static String getNormalizedUri(HttpServletRequest hr) {
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

    private Pattern withoutLoginPattern = Pattern.compile(
            "^/(?:login|tools|logout|api|_ah|css|js|bootstrap|images|coordinator-android.apk|favicon.ico)(?:/|$)"
    );

    private boolean isWithoutLoginRequest(HttpServletRequest hr) {
        return isUriMatch(hr, withoutLoginPattern);
    }

    public static boolean isUriStartsWith(HttpServletRequest hr, String prefix) {
        final String uriWithoutContext = getNormalizedUri(hr);
        return uriWithoutContext.startsWith(prefix);
    }

    private boolean isUriMatch(HttpServletRequest hr, Pattern pattern) {
        final String uriWithoutContext = getNormalizedUri(hr);
        return pattern.matcher(uriWithoutContext).find();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    private void redirectWithBacklink(String uriPath, HttpServletRequest hRequest, HttpServletResponse hResponse) throws IOException {
        String root = hRequest.getContextPath();
        Url url = new Url(hRequest.getRequestURL().toString());
        String[] path = url.getPath();
        String urlPath = root + uriPath;
        if (path != null && path.length > 0 && !ValueTool.isEmpty(path[0])) {
            urlPath +=
                urlPath.indexOf("?") == -1 ? "?" : "&"
                + "retUrl=" + Url.encode(hRequest.getRequestURL().toString()+"?"+hRequest.getQueryString());
        }
        hResponse.sendRedirect(urlPath);
    }
}
