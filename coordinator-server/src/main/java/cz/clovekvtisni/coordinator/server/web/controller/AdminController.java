package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController extends AbstractController {

    @RequestMapping
    public String show() {
        return "admin/home";
    }

    public static Breadcrumb getBreadcrumb() {
        return new Breadcrumb("/admin", "breadcrumb.admin");
    }

    @ModelAttribute("breadcrumbs")
    public Breadcrumb[] breadcrumbs() {
        return new Breadcrumb[] {
                UserListController.getBreadcrumb(),
                EventListController.getBreadcrumb()
        };
    }
}
