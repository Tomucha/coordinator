package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/user/list")
public class UserListController extends AbstractController {

    @Autowired
    private UserService userService;

    @CheckPermission("#helper.canUpdate(#entity)")
    @RequestMapping
    public String list(@RequestParam(value = "bookmark", required = false) String bookmark, Model model) {
        UserEntity admin = getLoggedUser();
        UserFilter filter = new UserFilter();
        filter.setOrganizationIdVal(admin.getOrganizationId());

        model.addAttribute("userResult", userService.findByFilter(filter, 5, bookmark, 0l));

        return "admin/user-list";
    }

}
