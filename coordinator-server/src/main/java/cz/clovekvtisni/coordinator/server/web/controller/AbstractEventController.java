package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.web.model.FilterParams;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.ui.Model;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
public abstract class AbstractEventController extends AbstractController {

    protected void populateEventModel(Model model, FilterParams params) {
        model.addAttribute("params", params);
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
                    AdminController.getBreadcrumb(),
                    EventMapController.getBreadcrumb(params),
                    EventUserListController.getBreadcrumb(params),
                    EventPlaceListController.getBreadcrumb(params),
                    EventDetailController.getBreadcrumb(params),
                    EventEditController.getBreadcrumb(params)
            };
        }
    }
}
