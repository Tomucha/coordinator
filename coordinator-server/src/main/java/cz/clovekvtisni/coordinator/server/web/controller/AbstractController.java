package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.*;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:59 PM
 */
public abstract class AbstractController {

    protected static int DEFAULT_LIST_LENGTH = 30;

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected MessageSource messageSource;

    @Autowired
    protected AppContext appContext;

    @Autowired
    protected SecurityTool securityTool;

    @Autowired
    protected PoiService poiService;

    @Autowired
    protected AuthorizationTool authorizationTool;

    @Autowired
    protected CoordinatorConfig config;

    @Autowired
    protected UserService userService;

    @Autowired
    private EventService eventService;

    @ModelAttribute
    public void loggedUser(Model model) {
        PermissionCheckResultModel checkResultModel = securityTool.checkServicePermissions(new ServicePermissionCheckDescriptor[]{
                new ServicePermissionCheckDescriptor("logout", UserService.class, "logout", new Class[]{}, new Object[]{})
        });
        model.addAttribute("loggedUser", appContext.getLoggedUser());
        model.addAttribute("canDoLogout", checkResultModel.isPermitted("logout"));
    }

    @ModelAttribute("now")
    public Long now() {
        return System.currentTimeMillis();
    }

    protected void addFormError(BindingResult errors, MaException exception) {
        errors.addError(new ObjectError("globalErrors", exception.getMessage()));
    }

    protected void addFormError(BindingResult errors, String errorCode, Object... params) {
        errors.addError(new ObjectError("globalErrors", getMessage(errorCode, params)));
    }

    protected void addFieldError(BindingResult errors, String modelAttribute, String fieldName, Object fieldValue, String errCode, Object... errParams) {
        errors.addError(new FieldError(modelAttribute, fieldName, fieldValue, false, null, null, getMessage(errCode, errParams)));
    }


    protected String getMessage(String code, Object... params) {
        return messageSource.getMessage(code, params, getLocale());
    }

    protected Locale getLocale() {
        return appContext.getLocale();
    }

    protected UserEntity getLoggedUser() {
        return appContext.getLoggedUser();
    }

    protected boolean isSuperAdmin(UserEntity user) {
        return authorizationTool.hasRole(AuthorizationTool.SUPERADMIN, user);
    }

    protected boolean isAdmin(UserEntity user) {
        return authorizationTool.hasRole(AuthorizationTool.ADMIN, user);
    }

    protected boolean isBackendAdmin(UserEntity user) {
        return authorizationTool.hasRole(AuthorizationTool.BACKEND, user);
    }

    @ModelAttribute("config")
    public CoordinatorConfig config() {
        return config;
    }

    protected UserEntity loadUserById(Long id, long flags) {
        UserEntity user = userService.findById(id, flags);
        if (user == null)
            throw NotFoundException.idNotExist("userEntity", id);
        return user;
    }

    protected EventEntity loadEventById(Long eventId) {
        if (eventId == null) throw NotFoundException.idNotExist();
        EventEntity event = eventService.findById(eventId, EventService.FLAG_FETCH_LOCATIONS);
        if (event == null)
            throw NotFoundException.idNotExist();

        return event;
    }

    @ModelAttribute("lastPoiList")
    public List<PoiEntity> populateLastPoiList(HttpServletRequest request) {
        UserEntity loggedUser = getLoggedUser();
        if (loggedUser == null)
            return new ArrayList<PoiEntity>(0);
        String eventId = request.getParameter("eventId");
        ResultList<PoiEntity> result;
        if (eventId != null)
            try {
                result = poiService.findLastByEventId(Long.parseLong(eventId));

            } catch (NumberFormatException e) {
                return new ArrayList<PoiEntity>(0);
            }
        else
            result = poiService.findLast(loggedUser.getOrganizationId());

        return result.getResult();
    }
}
