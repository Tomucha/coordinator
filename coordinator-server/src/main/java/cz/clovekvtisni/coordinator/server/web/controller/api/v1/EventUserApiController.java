package cz.clovekvtisni.coordinator.server.web.controller.api.v1;

import cz.clovekvtisni.coordinator.api.request.EventUserListRequestParams;
import cz.clovekvtisni.coordinator.api.request.UserUpdatePositionRequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.EventUserListResponseData;
import cz.clovekvtisni.coordinator.api.response.UserUpdatePositionResponseData;
import cz.clovekvtisni.coordinator.domain.AbstractModifiableEntity;
import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.util.EntityTool;
import cz.clovekvtisni.coordinator.server.web.controller.api.AbstractApiController;
import cz.clovekvtisni.coordinator.util.RunnableWithResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/api/v1/event/user")
public class EventUserApiController extends AbstractApiController {

    @Autowired
    private UserInEventService userInEventService;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public @ResponseBody ApiResponse filter(HttpServletRequest request) {

        EventUserListRequestParams params = parseRequest(request, EventUserListRequestParams.class);
        UserInEventFilter filter = new UserInEventFilter();
        filter.setEventIdVal(params.getEventId());
        filter.setModifiedDateVal(params.getModifiedFrom());
        filter.setModifiedDateOp(Filter.Operator.GT);
        filter.setOrder("modifiedDate");
        ResultList<UserInEventEntity> result = userInEventService.findByFilter(filter, 0, null, UserInEventService.FLAG_FETCH_USER);
        List<UserInEvent> userInEvents = new EntityTool().buildTargetEntities(result.getResult());

        return okResult(new EventUserListResponseData(userInEvents));
    }


    @RequestMapping(value = "/update-position", method = RequestMethod.POST)
    public @ResponseBody ApiResponse updatePosition(HttpServletRequest request) {
        UserUpdatePositionRequestParams params = parseRequest(request, UserUpdatePositionRequestParams.class);
        UserInEventFilter filter = new UserInEventFilter();
        filter.setEventIdVal(params.getEventId());
        filter.setUserIdVal(getLoggedUser().getId());
        ResultList<UserInEventEntity> found = userInEventService.findByFilter(filter, 1, null, 0l);

        if (found.getResultSize() == 0)
            throw NotFoundException.idNotExist();
        final UserInEventEntity userInEvent = found.firstResult();
        userInEvent.setLastLocationLatitude(params.getLatitude());
        userInEvent.setLastLocationLongitude(params.getLongitude());

        UserInEventEntity updated = securityTool.runWithAnonymousEnabled(new RunnableWithResult<UserInEventEntity>() {
            @Override
            public UserInEventEntity run() {
                return userInEventService.update(userInEvent);
            }
        });

        return okResult(new UserUpdatePositionResponseData(updated.buildTargetEntity()));
    }
}
