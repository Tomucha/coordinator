package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import cz.clovekvtisni.coordinator.server.web.model.FilterParams;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
@Controller
@RequestMapping("/admin/event/users")
public class EventUsersController extends AbstractEventController {

    @Autowired
    private UserInEventService userInEventService;

    @RequestMapping(method = RequestMethod.GET)
    public String listUsers(@ModelAttribute("params") EventFilterParams params, Model model) {
        if (params.getEventId() == null)
            throw NotFoundException.idNotExist();

        UserInEventFilter inEventFilter = new UserInEventFilter();
        inEventFilter.setEventIdVal(params.getEventId());
        if (params.getUserFulltext() != null) {
            final String fullText = params.getUserFulltext();
            inEventFilter.addAfterLoadCallback(new Filter.AfterLoadCallback<UserInEventEntity>() {
                @Override
                public boolean accept(UserInEventEntity entity) {
                    UserEntity user = entity.getUserEntity();
                    return user.getFullName() != null && user.getFullName().contains(fullText);
                }
            });
        }
        ResultList<UserInEventEntity> userInEvents = userInEventService.findByFilter(inEventFilter, 0, null, UserInEventService.FLAG_FETCH_USER);
        model.addAttribute("userInEvents", userInEvents.getResult());

        populateEventModel(model, new EventFilterParams(params.getEventId()));

        return "admin/event-users";
    }

    public static Breadcrumb getBreadcrumb(FilterParams params) {
        return new Breadcrumb(params, "/admin/event/users", "breadcrumb.eventUsers");
    }
}
