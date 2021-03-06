package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.util.ValueTool;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/27/12
 * Time: 5:50 PM
 */
@Controller
@RequestMapping("/login")
public class LoginController extends AbstractSuperadminController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String show(Model model, @RequestParam(value = "retUrl", required = false) String retUrl, HttpServletRequest request) {
	    String root = System.getProperty("cz.clovekvtisni.coordinator.urlRoot");
	    logger.info("Expected root: "+root);
	    if (!request.isSecure() && root.startsWith("https") && !request.getServerName().equals("localhost")) {
		    // we should redirect
		    return "redirect:"+root+"/login";
	    }
        LoginForm form = new LoginForm();
        form.retUrl = retUrl;
        model.addAttribute("user", form);

        return "public/login";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String login(@ModelAttribute("user") @Valid LoginForm loginForm, BindingResult errors) {
        if (errors.hasErrors()) {
            return "public/login";
        }

        String retUrl;
        try {
            userService.loginWeb(loginForm.email, loginForm.password);

            retUrl = loginForm.retUrl;
            if (ValueTool.isEmpty(retUrl)) {
                retUrl = "/superadmin";
            }
        } catch (MaPermissionDeniedException ex) {
            addFormError(errors, ex);
            return "public/login";
        }

        return "redirect:" + retUrl;
    }

    @RequestMapping(value = "/ajax/lost-password", method = RequestMethod.GET)
    public String ajaxLostPassword(@RequestParam String email, Model model) {
        if (!userService.lostPassword(email)) {
            model.addAttribute("notExist", true);
        }

        return "ajax/lost-password";
    }


    public static class LoginForm {

        @NotEmpty
        private String email;

        @NotEmpty
        private String password;

        private String retUrl;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRetUrl() {
            return retUrl;
        }

        public void setRetUrl(String retUrl) {
            this.retUrl = retUrl;
        }
    }

}
