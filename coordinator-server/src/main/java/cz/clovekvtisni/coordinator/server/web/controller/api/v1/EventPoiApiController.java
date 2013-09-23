package cz.clovekvtisni.coordinator.server.web.controller.api.v1;

import cz.clovekvtisni.coordinator.api.request.EventPoiCreateRequestParams;
import cz.clovekvtisni.coordinator.api.request.EventPoiListRequestParams;
import cz.clovekvtisni.coordinator.api.request.EventPoiTransitionRequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.EventPoiFilterResponseData;
import cz.clovekvtisni.coordinator.api.response.EventPoiResponseData;
import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.domain.config.RolePermission;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.security.SecurityTool;
import cz.clovekvtisni.coordinator.server.security.permission.TransitionPermission;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.util.EntityTool;
import cz.clovekvtisni.coordinator.server.web.controller.api.AbstractApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/api/v1/event/poi")
public class EventPoiApiController extends AbstractApiController {

    @Autowired
    private PoiService poiService;

    @Autowired
    private SecurityTool securityTool;

    @Autowired
    private AuthorizationTool authorizationTool;

    @RequestMapping(method = RequestMethod.POST, value = "/list")
    public @ResponseBody ApiResponse filter(HttpServletRequest request) {
        EventPoiListRequestParams params = parseRequest(request, EventPoiListRequestParams.class);
        PoiFilter filter = new PoiFilter();
        filter.setEventIdVal(params.getEventId());
        if (params.getModifiedFrom() != null) {
            filter.setModifiedDateVal(params.getModifiedFrom());
            filter.setModifiedDateOp(Filter.Operator.GT);
        }
        UserEntity loggedUser = getLoggedUser();
        if (!authorizationTool.hasAnyPermission(loggedUser, RolePermission.EDIT_POI_IN_ORG))
            filter.setVisibleForRolesVal(loggedUser.getRoleIdList());

        filter.setOrder("modifiedDate");

        ResultList<PoiEntity> result = poiService.findByFilter(filter, 0, null, 0l);

        SecurityTool.SecurityHelper helper = securityTool.buildHelper();

        List<Poi> poiList = new ArrayList<Poi>();

        for (Iterator<PoiEntity> iterator = result.getResult().iterator(); iterator.hasNext(); ) {
            PoiEntity next = iterator.next();
            Poi p = buildDetailedPoiEntity(helper, next);
            poiList.add(p);
        }

        return okResult(new EventPoiFilterResponseData(poiList));
    }

    private Poi buildDetailedPoiEntity(SecurityTool.SecurityHelper helper, PoiEntity next) {
        Poi p = next.buildTargetEntity();
        TransitionPermission transitionPermission = new TransitionPermission(next);
        p.setCanEdit(helper.canUpdate(next));
        p.setCanDoTransition(helper.canDo(transitionPermission));
        return p;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/transition")
    public @ResponseBody ApiResponse workflow(HttpServletRequest request) {
        EventPoiTransitionRequestParams params = parseRequest(request, EventPoiTransitionRequestParams.class);

        PoiEntity poi = poiService.findById(params.getPoiId(), 0);
        poi = poiService.transitWorkflowState(poi, params.getTransitionId(), params.getComment(), 0l);

        SecurityTool.SecurityHelper helper = securityTool.buildHelper();

        return okResult(new EventPoiResponseData(buildDetailedPoiEntity(helper, poi)));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public @ResponseBody ApiResponse create(HttpServletRequest request) {
        EventPoiCreateRequestParams params = parseRequest(request, EventPoiCreateRequestParams.class);

        PoiEntity newPoi = new PoiEntity();
        newPoi.setLatitude(params.getLatitude());
        newPoi.setLongitude(params.getLongitude());
        newPoi.setName(params.getName());
        newPoi.setDescription(params.getDescription());
        newPoi.setEventId(params.getEventId());
        newPoi.setPoiCategoryId(params.getPoiCategoryId());
        newPoi.setOrganizationId(getLoggedUser().getOrganizationId());

        newPoi = poiService.createPoi(newPoi);

        SecurityTool.SecurityHelper helper = securityTool.buildHelper();

        return okResult(new EventPoiResponseData(buildDetailedPoiEntity(helper, newPoi)));
    }

}
