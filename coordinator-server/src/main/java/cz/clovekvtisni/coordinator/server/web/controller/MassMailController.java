package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.web.model.MailForm;
import cz.clovekvtisni.coordinator.server.web.util.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping("/superadmin/mail")
public class MassMailController extends AbstractSuperadminController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String massMailPrepare(@ModelAttribute("params") MailForm params, Model model) {
        return "superadmin/mail";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String massMail(@ModelAttribute("params") @Valid MailForm params, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "superadmin/mail";
        }

        userService.emailAllUsers(params.getSubject(), params.getBody(), null);
        setGlobalMessage(getMessage("msg.sendingEmails"), model);
        return "superadmin/mail";
    }


    public static Breadcrumb getBreadcrumb() {
        return new Breadcrumb(null, "/superadmin/mail", "breadcrumb.mail");
    }

}
