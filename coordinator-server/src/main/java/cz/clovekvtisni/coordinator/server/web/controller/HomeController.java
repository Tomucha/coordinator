package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class HomeController extends AbstractController {

    @RequestMapping
    public String show() {
        return "home";
    }

    public static Breadcrumb getBreadcrumb() {
        return new Breadcrumb("/admin", "breadcrumb.home");
    }

    @ModelAttribute("breadcrumbs")
    public Breadcrumb[] breadcrumbs() {
        return new Breadcrumb[] {
                UserListController.getBreadcrumb(),
                EventListController.getBreadcrumb()
        };
    }
}
