package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/superadmin")
public class SuperadminController extends AbstractSuperadminController {

    @RequestMapping
    public String show() {
        return "superadmin/home";
    }

    public static Breadcrumb getBreadcrumb() {
        return new Breadcrumb(null, "/superadmin", "breadcrumb.home");
    }

}
