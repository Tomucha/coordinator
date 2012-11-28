package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.tool.objectify.UniqueKeyViolation;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.model.EventUserForm;
import cz.clovekvtisni.coordinator.server.web.model.FilterParams;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
@Controller
@RequestMapping("/admin/event/user/edit")
public class EventUserEditController extends AbstractEventController {

    @Autowired
    private UserInEventService userInEventService;

    @RequestMapping(method = RequestMethod.GET)
    public String edit(@ModelAttribute("params") EventFilterParams params, @RequestParam(value = "userId", required = false) Long userId, Model model) {

        EventUserForm form = new EventUserForm();
        form.injectConfigValues(appContext, authorizationTool, config);
        form.setEventId(params.getEventId());

        if (userId != null) {
            UserEntity user = loadUserById(userId, 0l);
            form.populateFrom(user);
            UserInEventEntity inEvent = fetchUserInEvent(params.getEventId(), userId);
            if (inEvent != null)
                form.setUserInEventId(inEvent.getId());
        } else
            form.setOrganizationId(getLoggedUser().getOrganizationId());

        model.addAttribute("form", form);
        populateEventModel(model, params);

        return "admin/event-user-edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createOrUpdate(@ModelAttribute("form") @Valid EventUserForm form, BindingResult bindingResult, Model model) {
        form.injectConfigValues(appContext, authorizationTool, config);
        form.postValidate(bindingResult, messageSource, appContext.getLocale());

        if (bindingResult.hasErrors()) {
            populateEventModel(model, new EventFilterParams(form.getEventId()));
            return "admin/event-user-edit";
        }

        try {
            // TODO begin transaction ?
            UserEntity user = new UserEntity().populateFrom(form);
            if (form.isNew()) {
                form.setRoleIdList(new String[] {AuthorizationTool.ANONYMOUS});
                user = userService.createUser(user);
            } else
                user = userService.updateUser(user);

            if (form.getUserInEventId() == null) {
                form.setId(user.getId());
                userInEventService.create(form.buildUserInEventEntity());
            }

            return "redirect:/admin/event/users?eventId=" + form.getEventId();

        } catch (UniqueKeyViolation e) {
            addFieldError(bindingResult, "form", e.getProperty().toString().toLowerCase(), form.getEmail(), "error.UNIQUE_KEY_VIOLATION");
            populateEventModel(model, new EventFilterParams(form.getEventId()));
            return "admin/event-user-edit";

        } catch (MaException e) {
            addFormError(bindingResult, e);
            populateEventModel(model, new EventFilterParams(form.getEventId()));
            return "admin/event-user-edit";
        }
    }

    private UserInEventEntity fetchUserInEvent(Long eventId, Long userId) {
        if (eventId == null)
            throw NotFoundException.idNotExist();

        if (userId == null)
            return null;

        UserInEventFilter filter = new UserInEventFilter();
        filter.setEventIdVal(eventId);
        filter.setUserIdVal(userId);

        UserInEventEntity inEventEntity = userInEventService.findByFilter(filter, 0, null, UserInEventService.FLAG_FETCH_USER).firstResult();

        return inEventEntity;
    }

    public static Breadcrumb getBreadcrumb(FilterParams params) {
        return new Breadcrumb(params, "/admin/event/users", "breadcrumb.eventUsers");
    }
}
