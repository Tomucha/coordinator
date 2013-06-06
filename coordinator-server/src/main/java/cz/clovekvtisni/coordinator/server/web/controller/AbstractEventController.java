package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.domain.ActivityEntity;
import cz.clovekvtisni.coordinator.server.security.SecurityTool;
import cz.clovekvtisni.coordinator.server.service.ActivityService;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
public abstract class AbstractEventController extends AbstractController {

    @Autowired
    private ActivityService activityService;

    @ModelAttribute("activity")
    protected List<ActivityEntity> activityLog(
            @RequestParam(value = "poiId", required = false) Long poiId,
            @RequestParam(value = "userId", required = false) Long userId) {

        Long eventId = null;
        if (appContext.getActiveEvent() != null) {
            eventId = appContext.getActiveEvent().getId();
        }

        List<ActivityEntity> result = activityService.find(eventId, poiId, userId, null, ActivityService.FLAG_FETCH_ALL).getResult();
        logger.info("Activity to model: "+result);
        return result;
    }

    @ModelAttribute("breadcrumbs")
    protected Breadcrumb[] breadcrumbs() {
        return new Breadcrumb[]{
                // EventMapController.getBreadcrumb(appContext.getActiveEvent()),
                EventPoiListController.getBreadcrumb(appContext.getActiveEvent()),
                EventUserListController.getBreadcrumb(appContext.getActiveEvent()),
                EventUserGroupListController.getBreadcrumb(appContext.getActiveEvent()),
                EventDetailController.getBreadcrumb(appContext.getActiveEvent())
        };
    }

}
