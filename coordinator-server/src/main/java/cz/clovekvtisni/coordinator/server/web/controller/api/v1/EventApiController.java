package cz.clovekvtisni.coordinator.server.web.controller.api.v1;

import com.googlecode.objectify.Key;
import cz.clovekvtisni.coordinator.api.request.EventFilterRequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.EventFilterResponseData;
import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.service.OrganizationInEventService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.util.EntityTool;
import cz.clovekvtisni.coordinator.server.web.controller.api.AbstractApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/api/v1/event")
public class EventApiController extends AbstractApiController {

    @Autowired
    private EventService eventService;

    @Autowired
    private OrganizationInEventService organizationInEventService;

    @Autowired
    private UserInEventService userInEventService;

    @RequestMapping(value = "/registered", method = RequestMethod.POST)
    public @ResponseBody ApiResponse filter(HttpServletRequest request) {
        UserRequest<EventFilterRequestParams> req = parseRequestAnonymous(request, EventFilterRequestParams.class);
        EventFilterResponseData responseData = new EventFilterResponseData();

        Set<Long> eventIds = new HashSet<Long>();

        if (req.params.getOrganizationId() != null) {
            OrganizationInEventFilter filter = new OrganizationInEventFilter();
            filter.setOrganizationIdVal(req.params.getOrganizationId());
            ResultList<OrganizationInEventEntity> eventsByOrg = organizationInEventService.findByFilter(filter, 0, null, 0l);
            responseData.setOrganizationInEvents(new EntityTool().buildTargetEntities(eventsByOrg.getResult()));
            for (OrganizationInEventEntity entity : eventsByOrg.getResult()) {
                eventIds.add(entity.getEventId());
            }
        }
        if (req.params.getUserId() != null) {
            UserInEventFilter filter = new UserInEventFilter();
            filter.setUserIdVal(req.params.getUserId());
            ResultList<UserInEventEntity> eventsByUser = userInEventService.findByFilter(filter, 0, null, 0l);
            responseData.setUserInEvents(new EntityTool().buildTargetEntities(eventsByUser.getResult()));
            for (UserInEventEntity entity : eventsByUser.getResult()) {
                eventIds.add(entity.getEventId());
            }
        }

        if (eventIds.size() > 0) {
            List<EventEntity> events = eventService.findByIds(EventService.FLAG_FETCH_LOCATIONS, eventIds.toArray(new Long[0]));
            responseData.setEvents(new EntityTool().buildTargetEntities(events));
        }

        return okResult(responseData);
    }
}