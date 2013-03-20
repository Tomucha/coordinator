package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.domain.*;
import cz.clovekvtisni.coordinator.server.service.OrganizationInEventService;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserGroupService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping("/admin/event/user/assigned")
public class EventUserAssignedToPoi extends AbstractEventController {

    @Autowired
    private UserInEventService userInEventService;

    @Autowired
    private PoiService poiService;


    @RequestMapping(method = RequestMethod.GET)
    public String list(
            @RequestParam(value = "eventId", required = true) long eventId,
            @RequestParam(value = "poiId", required = true) long poiId,
            @RequestParam(value = "userId", required = false) Long userId,
            Model model) {

        return returnAssignedUsers(model, poiService.findById(poiId, 0));
    }

    @RequestMapping(method = RequestMethod.POST, params = "assignUserId")
    public String assignUser(
            @RequestParam(value = "eventId", required = true) long eventId,
            @RequestParam(value = "poiId", required = true) long poiId,
            @RequestParam(value = "assignUserId", required = true) Long assignUserId,
            Model model) {
        PoiEntity poi = poiService.findById(poiId, 0);
        if (poi != null && assignUserId != null)
            poi = poiService.assignUser(poi, assignUserId);

        return returnAssignedUsers(model, poi);
    }

    @RequestMapping(method = RequestMethod.POST, params = "unassignUserId")
    public String unassignUser(
            @RequestParam(value = "eventId", required = true) long eventId,
            @RequestParam(value = "poiId", required = true) long poiId,
            @RequestParam(value = "unassignUserId", required = true) Long assignUserId,
            Model model) {
        PoiEntity poi = poiService.findById(poiId, 0);
        if (poi != null && assignUserId != null)
            poi = poiService.unassignUser(poi, assignUserId);

        return returnAssignedUsers(model, poi);
    }

    @RequestMapping(method = RequestMethod.POST, params = "assignUserGroupId")
    public String assignUserGroup(
            @RequestParam(value = "eventId", required = true) long eventId,
            @RequestParam(value = "poiId", required = true) long poiId,
            @RequestParam(value = "assignUserGroupId", required = true) Long assignUserGroupId,
            Model model) {

        PoiEntity poi = poiService.findById(poiId, 0);
        if (assignUserGroupId != null && poi != null) {
            ResultList<UserInEventEntity> inEvents = userInEventService.findByUserGroupId(eventId, assignUserGroupId, 0, null, 0l);
            for (UserInEventEntity inEvent : inEvents) {
                poi = poiService.assignUser(poi, inEvent.getUserId());
            }
        }

        return returnAssignedUsers(model, poi);
    }

    private String returnAssignedUsers(Model model, PoiEntity poi) {
        if (poi == null) {
            model.addAttribute("assignedUsers", new ArrayList<UserInEventEntity>(0));

        } else {
            Set<Long> userIds = poi.getUserIdList();

            List<UserInEventEntity> assignedUsers = userInEventService.findByIds(poi.getEventId(), userIds, 0l);
            model.addAttribute("assignedUsers", assignedUsers);
        }

        return "ajax/assigned-users";
    }}
