package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.model.UserFilterParams;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

@Controller
@RequestMapping("/superadmin/user/list")
public class UserListController extends AbstractSuperadminController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(@ModelAttribute("params") UserFilterParams params, @RequestParam(value = "bookmark", required = false) String bookmark, Model model) {
        UserEntity admin = getLoggedUser();
        UserFilter filter = new UserFilter();
        params.populateUserFilter(filter);

        if (!admin.isSuperadmin())
            filter.setOrganizationIdVal(admin.getOrganizationId());

        model.addAttribute("userResult", userService.findByFilter(filter, DEFAULT_LIST_LENGTH, bookmark, UserService.FLAG_FETCH_EQUIPMENT | UserService.FLAG_FETCH_SKILLS));

        return "superadmin/user-list";
    }
}
