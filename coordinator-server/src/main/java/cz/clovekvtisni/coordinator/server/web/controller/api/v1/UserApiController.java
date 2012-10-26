package cz.clovekvtisni.coordinator.server.web.controller.api.v1;

import cz.clovekvtisni.coordinator.api.request.LoginRequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.LoginResponseData;
import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.web.controller.api.AbstractApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/27/12
 * Time: 12:30 AM
 */
@Controller
@RequestMapping("/api/v1/user")
public class UserApiController extends AbstractApiController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody ApiResponse login(HttpServletRequest request) {
        LoginRequestParams params = parseParams(request, LoginRequestParams.class);
        UserEntity userEntity = userService.login(params.getLogin(), params.getPassword());
        if (userEntity == null) {
            throw MaPermissionDeniedException.wrongCredentials();
        }
        return okResult(new LoginResponseData(userEntity.buildUser()));
    }
}
