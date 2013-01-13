package cz.clovekvtisni.coordinator.server.web.controller.api.v1;

import com.googlecode.objectify.Key;
import cz.clovekvtisni.coordinator.api.request.EventFilterRequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.EventFilterResponseData;
import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
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
import java.util.ArrayList;
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
        EventFilterRequestParams params = parseRequestAnonymous(request, EventFilterRequestParams.class);
        EventFilterResponseData responseData = new EventFilterResponseData();

        Set<Long> eventIds = new HashSet<Long>();

        if (params.getOrganizationId() == null)
            throw new IllegalArgumentException("organization id cannot be null");

        UserEntity user = getLoggedUser();
        if (user != null) {
            responseData.setOrganizationInEvents(getEventsByOrganizationId(params.getOrganizationId(), eventIds));
            UserInEventFilter filter = new UserInEventFilter();
            filter.setUserIdVal(user.getId());
            ResultList<UserInEventEntity> eventsByUser = userInEventService.findByFilter(filter, 0, null, 0l);
            List<UserInEventEntity> byOrganization = new ArrayList<UserInEventEntity>();
            for (UserInEventEntity entity : eventsByUser) {
                if (eventIds.contains(entity.getEventId()))
                    byOrganization.add(entity);
            }
            responseData.setUserInEvents(new EntityTool().buildTargetEntities(byOrganization));

        } else if (params.getOrganizationId() != null) {
            responseData.setOrganizationInEvents(getEventsByOrganizationId(params.getOrganizationId(), eventIds));
        }

        if (eventIds.size() > 0) {
            List<EventEntity> events = eventService.findByIds(EventService.FLAG_FETCH_LOCATIONS, eventIds.toArray(new Long[0]));
            responseData.setEvents(new EntityTool().buildTargetEntities(events));
        }

        return okResult(responseData);
    }

    private List<OrganizationInEvent> getEventsByOrganizationId(String organizationId, Set<Long> eventIds) {
        OrganizationInEventFilter filter = new OrganizationInEventFilter();
        filter.setOrganizationIdVal(organizationId);
        ResultList<OrganizationInEventEntity> eventsByOrg = organizationInEventService.findByFilter(filter, 0, null, 0l);
        for (OrganizationInEventEntity entity : eventsByOrg.getResult()) {
            eventIds.add(entity.getEventId());
        }

        return new EntityTool().buildTargetEntities(eventsByOrg.getResult());
    }
}
