package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.EventFilter;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tomucha
 * Date: 24.01.13
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSuperadminController extends AbstractController {

    @ModelAttribute("rootBreadcrumb")
    public Breadcrumb rootBreadcrumb() {
        return new Breadcrumb(null, "/superadmin", "breadcrumb.superadmin");
    }

    @ModelAttribute("currentEvents")
    public List<EventEntity> populateCurrentEvents(HttpServletRequest request) {
        UserEntity loggedUser = getLoggedUser();
        if (loggedUser == null)
            return new ArrayList<EventEntity>(0);

        // TODO: pouze aktivni eventy
        return eventService.findByFilter(new EventFilter(), 10, null, 0).getResult();
    }


    @ModelAttribute("breadcrumbs")
    public final Breadcrumb[] breadcrumbs() {
        return new Breadcrumb[] {
                UserListController.getBreadcrumb(),
                EventListController.getBreadcrumb(),
                MassMailController.getBreadcrumb()
        };
    }

}
