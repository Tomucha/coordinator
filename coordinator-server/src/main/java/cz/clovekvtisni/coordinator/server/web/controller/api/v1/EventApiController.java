package cz.clovekvtisni.coordinator.server.web.controller.api.v1;

import com.googlecode.objectify.Key;
import cz.clovekvtisni.coordinator.api.request.EventFilterRequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.EventFilterResponseData;
import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.domain.config.PoiCategory;
import cz.clovekvtisni.coordinator.server.domain.*;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.security.SecurityTool;
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
    SecurityTool securityTool;

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
                if (eventIds.contains(entity.getEventId())) {
                    byOrganization.add(entity);
                }
            }

            // nyni overime prava uzivatele pro vytvareni zaznamu POI,
            // seznam kategorii, ve kterych smi vytvaret, si posleme na klienta
            List<UserInEvent> userInEvents = new EntityTool().buildTargetEntities(byOrganization);
            for (UserInEvent userInEvent : userInEvents) {
                appContext.setActiveUserInEvent(
                        userInEventService.findById(userInEvent.getEventId(), userInEvent.getUserId(),
                        UserInEventService.FLAG_FETCH_EVENT | UserInEventService.FLAG_FETCH_GROUPS ));

                List<PoiCategory> openedPoiCategories = new ArrayList<PoiCategory>();
                for (PoiCategory poiCategory : config.getPoiCategoryList()) {
                    PoiEntity p = new PoiEntity();
                    p.setPoiCategory(poiCategory);
                    p.setPoiCategoryId(poiCategory.getId());
                    p.setEventId(userInEvent.getEventId());
                    p.setOrganizationId(user.getOrganizationId());
                    if (securityTool.buildHelper().canCreate(p)) {
                        openedPoiCategories.add(poiCategory);
                    }
                }
                //logger.info("Opened PoiCategories: "+openedPoiCategories);
                userInEvent.setOpenedCategories(openedPoiCategories.toArray(new PoiCategory[0]));
            }
            responseData.setUserInEvents(userInEvents);

        } else if (params.getOrganizationId() != null) {
            responseData.setOrganizationInEvents(getEventsByOrganizationId(params.getOrganizationId(), eventIds));
        }

        return okResult(responseData);
    }

    private List<OrganizationInEvent> getEventsByOrganizationId(String organizationId, Set<Long> eventIds) {
        OrganizationInEventFilter filter = new OrganizationInEventFilter();
        filter.setOrganizationIdVal(organizationId);
        ResultList<OrganizationInEventEntity> eventsByOrg = organizationInEventService.findByFilter(filter, 0, null, OrganizationInEventService.FLAG_FETCH_EVENT);
        for (OrganizationInEventEntity entity : eventsByOrg.getResult()) {
            eventIds.add(entity.getEventId());
        }

        return new EntityTool().buildTargetEntities(eventsByOrg.getResult());
    }
}
