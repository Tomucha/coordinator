package cz.clovekvtisni.coordinator.server.web.controller.api.v1;

import cz.clovekvtisni.coordinator.api.request.EmptyRequestParams;
import cz.clovekvtisni.coordinator.api.request.OrganizationEventsRequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
import cz.clovekvtisni.coordinator.api.response.OrganizationEventsResponseData;
import cz.clovekvtisni.coordinator.domain.AbstractModifiableEntity;
import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.domain.config.*;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
import cz.clovekvtisni.coordinator.server.service.OrganizationInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.util.EntityTool;
import cz.clovekvtisni.coordinator.server.web.controller.api.AbstractApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 7.11.12
 */
@Controller
@RequestMapping("/api/v1/organization")
public class OrganizationApiController extends AbstractApiController {

    @Autowired
    OrganizationInEventService organizationInEventService;

    @RequestMapping("/events")
    public @ResponseBody ApiResponse organizationEvents(HttpServletRequest request) {
        UserRequest<OrganizationEventsRequestParams> req = parseRequestAnonymous(request, OrganizationEventsRequestParams.class);

        OrganizationInEventFilter filter = new OrganizationInEventFilter();
        if (req.params.getOrganizationId() != null)
            filter.setOrganizationIdVal(req.params.getOrganizationId());

        else if (req.user != null)
            filter.setOrganizationIdVal(req.user.getOrganizationId());

        List<OrganizationInEventEntity> found = organizationInEventService.findByFilter(filter, 0, null, OrganizationInEventService.FLAG_FETCH_EVENT).getResult();
        List<OrganizationInEvent> result = new EntityTool().buildTargetEntities(found);

        return okResult(new OrganizationEventsResponseData(result));
    }
}
