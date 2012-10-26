package cz.clovekvtisni.coordinator.server.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/index")
public class HomeController extends AbstractController {

    @RequestMapping
    public String show() {
        return "home";
    }

}
