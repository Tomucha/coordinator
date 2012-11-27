package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.service.EventService;
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
        EventEntity event = eventService.findById(eventId, EventService.FLAG_FETCH_LOCATIONS);
        if (event == null)
            throw NotFoundException.idNotExist();

        return event;
    }

    protected EventEntity populateEventModel(Model model, EventEntity event) {
        model.addAttribute("event", event);
        model.addAttribute("breadcrumbs", breadcrumbs(event));

        return event;
    }


    protected Breadcrumb[] breadcrumbs(EventEntity entity) {
        if (entity == null || entity.isNew()) {
            return new Breadcrumb[] {
                    UserListController.getBreadcrumb(),
                    EventListController.getBreadcrumb()
            };

        } else {
            return new Breadcrumb[] {
                    HomeController.getBreadcrumb(),
                    EventMapController.getBreadcrumb(entity),
                    EventUsersController.getBreadcrumb(entity),
                    EventPlacesController.getBreadcrumb(entity),
                    EventDetailController.getBreadcrumb(entity),
                    EventEditController.getBreadcrumb(entity)
            };
        }
    }
}
