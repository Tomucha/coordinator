package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.domain.config.Equipment;
import cz.clovekvtisni.coordinator.domain.config.Skill;
import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.service.OrganizationInEventService;
import cz.clovekvtisni.coordinator.server.service.UserGroupService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.UniqueKeyViolation;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.model.EventUserForm;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private OrganizationInEventService organizationInEventService;

    @RequestMapping(method = RequestMethod.GET)
    public String edit(@ModelAttribute("params") EventFilterParams params, @RequestParam(value = "userId", required = false) Long userId, Model model) {

        EventUserForm form = new EventUserForm();

        if (userId != null) {
            UserEntity user = loadUserById(userId, 0l);
            form.populateFrom(user);
            form.setUserId(user.getId());
            UserInEventEntity inEvent = fetchUserInEvent(params.getEventId(), userId);
            form.setEventId(inEvent.getEventId());
            form.setLastLocationLatitude(inEvent.getLastLocationLatitude());
            form.setLastLocationLongitude(inEvent.getLastLocationLongitude());
            if (inEvent != null) {
                if (inEvent.getGroupIdList() != null)
                    form.setGroupIdList(Arrays.asList(inEvent.getGroupIdList()));
            }
        } else {
            form.setOrganizationId(getLoggedUser().getOrganizationId());
            form.setRoleIdList(new String[]{AuthorizationTool.ANONYMOUS});
            form.setEventId(params.getEventId());
        }

        populateEventModel(model, params, form);

        return "admin/event-user-edit";
    }

    private void populateEventModel(Model model, EventFilterParams params, EventUserForm form) {

        EventFilterParams eventParams = (EventFilterParams) params;
        form.injectConfigValues(appContext, authorizationTool, config);

        if (getLoggedUser().getOrganizationId() != null) {
            OrganizationInEventFilter inEventFilter = new OrganizationInEventFilter();
            inEventFilter.setOrganizationIdVal(getLoggedUser().getOrganizationId());
            inEventFilter.setEventIdVal(params.getEventId());
            OrganizationInEventEntity inEventEntity = organizationInEventService.findByFilter(inEventFilter, 0, null, 0l).singleResult();

            Map<String,Equipment> equipmentMap = config.getEquipmentMap();
            List<Equipment> equipmentList = new ArrayList<Equipment>(config.getEquipmentList().size());
            for (String equipmentId : inEventEntity.getRegistrationEquipment())
                if (equipmentMap.containsKey(equipmentId))
                    equipmentList.add(equipmentMap.get(equipmentId));
            model.addAttribute("equipmentList", equipmentList);

            Map<String, Skill> skillMap = config.getSkillMap();
            List<Skill> skillList = new ArrayList<Skill>(config.getSkillList().size());
            for (String skillId : inEventEntity.getRegistrationSkills())
                if (skillMap.containsKey(skillId))
                    skillList.add(skillMap.get(skillId));
            model.addAttribute("skillList", skillList);

        } else {
            model.addAttribute("equipmentList", config.getEquipmentList());
            model.addAttribute("skillList", config.getSkillList());
        }

        model.addAttribute("userGroups", userGroupService.findByEventId(params.getEventId(), 0l));
        model.addAttribute("form", form);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createOrUpdate(@ModelAttribute("form") @Valid EventUserForm form, BindingResult bindingResult, Model model) {
        form.injectConfigValues(appContext, authorizationTool, config);
        form.postValidate(bindingResult, messageSource, appContext.getLocale());

        if (bindingResult.hasErrors()) {
            populateEventModel(model, new EventFilterParams(form.getEventId()), form);
            return "admin/event-user-edit";
        }

        try {
            // TODO begin transaction ?
            UserEntity user = new UserEntity().populateFrom(form);
            if (form.getUserId() == null) {
                form.setRoleIdList(new String[] {AuthorizationTool.ANONYMOUS});
                user = userService.createUser(user);
            } else {
                user.setId(form.getUserId());
                user = userService.updateUser(user);
            }

            // FIXME: tohle je neprt, puvodne to poznaval podle ID
            if (form.getCreatedDate() == null) {
                form.setId(user.getId());
                userInEventService.create(form.buildUserInEventEntity());

            } else {
                UserInEventEntity inEvent = userInEventService.findById(form.getEventId(), user.getId(), 0l);
                inEvent.setGroupIdList(form.getGroupIdList() == null ? null : form.getGroupIdList().toArray(new Long[0]));
                inEvent.setLastLocationLatitude(form.getLastLocationLatitude());
                inEvent.setLastLocationLongitude(form.getLastLocationLongitude());
                userInEventService.update(inEvent);
            }

            return "redirect:/admin/event/user/list?eventId=" + form.getEventId();

        } catch (UniqueKeyViolation e) {
            addFieldError(bindingResult, "form", e.getProperty().toString().toLowerCase(), form.getEmail(), "error.UNIQUE_KEY_VIOLATION");
            populateEventModel(model, new EventFilterParams(form.getEventId()), form);
            return "admin/event-user-edit";

        } catch (MaException e) {
            addFormError(bindingResult, e);
            populateEventModel(model, new EventFilterParams(form.getEventId()), form);
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

        UserInEventEntity inEventEntity = userInEventService.findByFilter(filter, 0, null, UserInEventService.FLAG_FETCH_USER | UserInEventService.FLAG_FETCH_GROUPS).firstResult();

        return inEventEntity;
    }

    public static Breadcrumb getBreadcrumb(EventEntity event) {
        return new Breadcrumb(event, "/admin/event/user/list", "breadcrumb.eventUsers");
    }
}
