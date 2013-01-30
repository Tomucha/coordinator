package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

@Controller
@RequestMapping("/superadmin/user/list")
public class UserListController extends AbstractSuperadminController {

    @Autowired
    private UserService userService;

    @RequestMapping
    public String list(@RequestParam(value = "bookmark", required = false) String bookmark, Model model) {
        UserEntity admin = getLoggedUser();
        UserFilter filter = new UserFilter();
        filter.setOrganizationIdVal(admin.getOrganizationId());

        model.addAttribute("userResult", userService.findByFilter(filter, DEFAULT_LIST_LENGTH, bookmark, 0l));

        return "superadmin/user-list";
    }

    public static Breadcrumb getBreadcrumb() {
        return new Breadcrumb("/superadmin/user/list", "breadcrumb.userList");
    }

}
