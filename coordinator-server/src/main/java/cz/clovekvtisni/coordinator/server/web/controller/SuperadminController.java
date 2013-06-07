package cz.clovekvtisni.coordinator.server.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/superadmin")
public class SuperadminController extends AbstractSuperadminController {

    @RequestMapping
    public String show() {
        return "superadmin/home";
    }
}
