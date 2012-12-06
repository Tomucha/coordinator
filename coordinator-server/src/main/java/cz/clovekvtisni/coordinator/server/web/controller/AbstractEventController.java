package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.web.model.FilterParams;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
public abstract class AbstractEventController extends AbstractController {

    @Autowired
    private EventService eventService;

    protected EventEntity getEventById(Long eventId) {
        if (eventId == null) throw NotFoundException.idNotExist();
        EventEntity event = eventService.findById(eventId, EventService.FLAG_FETCH_LOCATIONS);
        if (event == null)
            throw NotFoundException.idNotExist();

        return event;
    }

    protected void populateEventModel(Model model, FilterParams params) {
        model.addAttribute("event", params);
        model.addAttribute("breadcrumbs", breadcrumbs(params));
    }


    protected Breadcrumb[] breadcrumbs(FilterParams params) {
        if (params == null || params.toMap().size() == 0) {
            return new Breadcrumb[] {
                    UserListController.getBreadcrumb(),
                    EventListController.getBreadcrumb()
            };

        } else {
            return new Breadcrumb[] {
                    HomeController.getBreadcrumb(),
                    EventMapController.getBreadcrumb(params),
                    EventUserListController.getBreadcrumb(params),
                    EventPlaceListController.getBreadcrumb(params),
                    EventDetailController.getBreadcrumb(params),
                    EventEditController.getBreadcrumb(params)
            };
        }
    }
}
