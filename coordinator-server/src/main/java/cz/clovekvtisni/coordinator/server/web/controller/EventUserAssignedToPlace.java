package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.domain.*;
import cz.clovekvtisni.coordinator.server.service.OrganizationInEventService;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserGroupService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping("/admin/event/user/assigned")
public class EventUserAssignedToPlace extends AbstractEventController {

    @Autowired
    private UserInEventService userInEventService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private OrganizationInEventService organizationInEventService;

    @Autowired
    private PoiService poiService;


    @RequestMapping(method = RequestMethod.GET)
    public String list(
            @RequestParam(value = "eventId", required = true) long eventId,
            @RequestParam(value = "poiId", required = true) long poiId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "delete", required = false) Boolean delete,
            Model model) {

        PoiEntity poi = poiService.findById(poiId, 0);
        if (userId != null) {
            if (delete != null && delete == false) {
                poi = poiService.assignUser(poi, userId);
            } else {
                poi = poiService.unassignUser(poi, userId);
            }
        }

        Set<Long> userIds = poi.getUserIdList();

        List<UserInEventEntity> assignedUsers = userInEventService.findByIds(poi.getEventId(), userIds, UserInEventService.FLAG_FETCH_USER);
        model.addAttribute("assignedUsers", assignedUsers);

        return "ajax/assigned-users";
    }

}
