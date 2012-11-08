package cz.clovekvtisni.coordinator.server.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/admin/user")
public class UserController extends AbstractController {

    @RequestMapping
    public String list() {
        return "home";
    }

}
