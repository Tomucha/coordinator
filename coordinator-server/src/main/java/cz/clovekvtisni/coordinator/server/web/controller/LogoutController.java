package cz.clovekvtisni.coordinator.server.web.controller;

import cz.clovekvtisni.coordinator.server.service.UserService;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/27/12
 * Time: 5:50 PM
 */
@Controller
@RequestMapping("/logout")
public class LogoutController extends AbstractController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String show(@RequestParam(value = "retUrl", defaultValue = "/") String retUrl) {

        userService.logout();

        return "redirect:" + retUrl;
    }


    public static class LoginForm {

        @NotEmpty
        private String login;

        @NotEmpty
        private String password;

        private String retUrl;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
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
