package cz.clovekvtisni.coordinator.server.web.controller.api.v1;

import cz.clovekvtisni.coordinator.api.request.EventFilterRequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.EventFilterResponseData;
import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.util.EntityTool;
import cz.clovekvtisni.coordinator.server.web.controller.api.AbstractApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/api/v1/event")
public class EventApiController extends AbstractApiController {

    @Autowired
    private EventService eventService;

    @RequestMapping(value = "/filter", method = RequestMethod.POST)
    public @ResponseBody ApiResponse filter(HttpServletRequest request) {
        UserRequest<EventFilterRequestParams> req = parseRequest(request, EventFilterRequestParams.class);

        OrganizationInEventFilter filter = new OrganizationInEventFilter();
        filter.setOrganizationIdVal(req.user.getOrganizationId());

        ResultList<EventEntity> result = eventService.findByOrganizationFilter(filter, 0, null, 0l);
        List<Event> events = new EntityTool().buildTargetEntities(result.getResult());

        return okResult(new EventFilterResponseData(events));
    }
}
