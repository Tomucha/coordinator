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
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
@RequestMapping("/tools")
public class ToolsController extends AbstractController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST, value = "/massMailContinue")
    public String massMailContinue(
            @RequestParam(value = "subject", required = false) String subject,
            @RequestParam(value = "htmlBody", required = false) String htmlBody,
            @RequestParam(value = "cursor", required = false) String cursor,
            Model model) {
        logger.info("Continue email: "+subject+" / "+htmlBody);

        appContext.setSystemCall(true);

        userService.emailAllUsers(subject, htmlBody, cursor);
        return "public/mass-mail-continue";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/unsubscribe")
    public String unsubscribe(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "signature", required = false) String signature,
            Model model) {
        if (userService.unsubscribe(email, signature)) {
            return "public/unsubscribed";
        } else {
            return "public/not-unsubscribed";
        }
    }

}
