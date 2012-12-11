package cz.clovekvtisni.coordinator.server.web.controller.api.v1;

import cz.clovekvtisni.coordinator.api.request.EventPoiListRequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.EventPoiFilterResponseData;
import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
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
import java.util.List;

@Controller
@RequestMapping("/api/v1/event/poi/list")
public class EventPoiApiController extends AbstractApiController {

    @Autowired
    private PoiService poiService;

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody ApiResponse filter(HttpServletRequest request) {
        UserRequest<EventPoiListRequestParams> req = parseRequest(request, EventPoiListRequestParams.class);
        PoiFilter filter = new PoiFilter();
        filter.setEventIdVal(req.params.getEventId());
        if (req.params.getModifiedFrom() != null) {
            filter.setModifiedDateVal(req.params.getModifiedFrom());
            filter.setModifiedDateOp(Filter.Operator.GT);
        }
        filter.setOrder("modifiedDate");

        ResultList<PoiEntity> result = poiService.findByFilter(filter, 0, null, PoiService.FLAG_FETCH_FROM_CONFIG);
        List<Poi> pois = new EntityTool().buildTargetEntities(result.getResult());

        return okResult(new EventPoiFilterResponseData(pois));
    }
}
