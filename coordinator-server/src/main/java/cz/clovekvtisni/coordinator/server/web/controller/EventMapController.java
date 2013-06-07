package cz.clovekvtisni.coordinator.server.web.controller;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.service.UserGroupService;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.util.EntityTool;
import cz.clovekvtisni.coordinator.server.util.Location;
import cz.clovekvtisni.coordinator.server.web.EventPrerequisitiesRequired;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.model.PoiForm;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
@Controller
@RequestMapping("/admin/event/map")
public class EventMapController extends AbstractEventController {

    @Autowired
    private UserInEventService userInEventService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private PoiService poiService;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(method = RequestMethod.GET, value = "/api/address")
    public @ResponseBody Location addressSearch(@RequestParam(value = "query") String query) throws IOException {
        URLFetchService urlFetch = URLFetchServiceFactory.getURLFetchService();
        String searchUrl = "http://nominatim.openstreetmap.org/search?q="+ URLEncoder.encode(query, "UTF-8")+"&format=json&limit=1";
        HTTPResponse response = urlFetch.fetch(new URL(searchUrl));
        byte[] data = response.getContent();

        JsonNode node = objectMapper.readTree(data);

        // FIXME: empty results

        double lat = Double.parseDouble(node.get(0).get("lat").getTextValue());
        double lon = Double.parseDouble(node.get(0).get("lon").getTextValue());

        Location l = new Location();
        l.setLatitude(lat);
        l.setLongitude(lon);
        return l;
    }

    @RequestMapping(method = RequestMethod.GET)
    @EventPrerequisitiesRequired
    public String map(@ModelAttribute("params") EventFilterParams params, Model model) {
        if (params.getEventId() == null)
            throw NotFoundException.idNotExist();

        UserInEventFilter userInEventFilter = new UserInEventFilter();
        userInEventFilter.setEventIdVal(appContext.getActiveEvent().getId());
        ResultList<UserInEventEntity> inEvents = userInEventService.findByFilter(userInEventFilter, 0, null, 0l);
        model.addAttribute("userInEventList", inEvents.getResult());

        PoiFilter poiFilter = new PoiFilter();
        poiFilter.setEventIdVal(appContext.getActiveEvent().getId());
        ResultList<PoiEntity> pois = poiService.findByFilter(poiFilter, 0, null, 0l);
        model.addAttribute("poiList", pois.getResult());

        model.addAttribute("userGroups", userGroupService.findByEventId(appContext.getActiveEvent().getId(), 0l));
        model.addAttribute("disableMap", true);

        return "admin/event-map";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/poi")
    public @ResponseBody List<PoiEntity> listPoi(
            @ModelAttribute("params") EventFilterParams params,
            @RequestParam(required = true) double latN,
            @RequestParam(required = true) double lonE,
            @RequestParam(required = true) double latS,
            @RequestParam(required = true) double lonW
    ) {
        return poiService.findByFilterAndBox(params.populatePoiFilter(new PoiFilter()), latN, lonE, latS, lonW, 0);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user")
    public @ResponseBody List<UserInEvent> listUsers(
            @ModelAttribute("params") EventFilterParams params,
            @RequestParam(required = true) double latN,
            @RequestParam(required = true) double lonE,
            @RequestParam(required = true) double latS,
            @RequestParam(required = true) double lonW
    ) {
        List<UserInEventEntity> result = userInEventService.findByFilterAndBox(params.populateUserInEventFilter(new UserInEventFilter()), latN, lonE, latS, lonW, 0l);
        return new EntityTool().buildTargetEntities(result);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/popup/poi")
    public String popupPoi(
            @RequestParam(value = "eventId", required = false) Long eventId,
            @RequestParam(value = "poiId", required = false) Long poiId,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "edit", required = false) Boolean edit,
            Model model) {

        if (poiId == null) {
            PoiForm form = new PoiForm();
            form.setEventId(eventId);
            form.setOrganizationId(getLoggedUser().getOrganizationId());
            form.setLatitude(latitude);
            form.setLongitude(longitude);
            model.addAttribute("poiForm", form);

            return "ajax/poi-popup-new";

        } else if (edit != null && edit) {
            PoiEntity poi = poiService.findById(poiId, 0);
            PoiForm form = new PoiForm();
            form.populateFrom(poi);
            model.addAttribute("poiForm", form);

            return "ajax/poi-popup-new";

        } else {
            return showPopupPoiForm(poiService.findById(poiId, 0), model);
        }
    }

    private String showPopupPoiForm(PoiEntity e, Model model) {
        model.addAttribute("poi", e);

        Set<Long> userIds = e.getUserIdList();
        List<UserInEventEntity> assignedUsers = userInEventService.findByIds(e.getEventId(), userIds, 0l);
        model.addAttribute("assignedUsers", assignedUsers);
        return "ajax/poi-popup";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/ajax/poi-update")
    public String ajaxPoiUpdate(@ModelAttribute("poiForm") @Valid PoiForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {

            // FIXME: refaktoring

            // populateEventModel(model, new EventFilterParams(form.getEventKey()));
            return "ajax/poi-popup-new";
        }

        PoiEntity poiEntity = new PoiEntity().populateFrom(form);

        try {
            if (poiEntity.isNew())
                poiEntity = poiService.createPoi(poiEntity);
            else
                poiEntity = poiService.updatePoi(poiEntity);

            return showPopupPoiForm(poiEntity, model);

        } catch (MaPermissionDeniedException e) {
            addFormError(bindingResult, e);

            // FIXME: refaktoring
            // populateEventModel(model, new EventFilterParams(form.getEventKey()));
            return "admin/event-poi-edit";
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/popup/user")
    public String popupUser(
            @RequestParam(value = "eventId", required = true) Long eventId,
            @RequestParam(value = "userId", required = true) Long userId,
            Model model) {

        UserInEventEntity u = userInEventService.findById(eventId, userId,
                UserInEventService.FLAG_FETCH_LAST_POI | UserInEventService.FLAG_FETCH_GROUPS
        );
        model.addAttribute("userInEvent", u);
        return "ajax/user-popup";
    }
}
