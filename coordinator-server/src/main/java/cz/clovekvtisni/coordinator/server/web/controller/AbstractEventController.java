package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
public abstract class AbstractEventController extends AbstractController {

    @ModelAttribute("breadcrumbs")
    protected Breadcrumb[] breadcrumbs() {
        return new Breadcrumb[]{
                EventUserListController.getBreadcrumb(appContext.getActiveEvent()),
                EventPlaceListController.getBreadcrumb(appContext.getActiveEvent()),
                EventMapController.getBreadcrumb(appContext.getActiveEvent()),
                EventDetailController.getBreadcrumb(appContext.getActiveEvent()),
                EventEditController.getBreadcrumb(appContext.getActiveEvent())
        };
    }

}