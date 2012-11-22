package cz.clovekvtisni.coordinator.server.web.controller.api.v1;

import cz.clovekvtisni.coordinator.api.request.LoginRequestParams;
import cz.clovekvtisni.coordinator.api.request.RegisterRequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.LoginResponseData;
import cz.clovekvtisni.coordinator.api.response.RegisterResponseData;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.exception.MaParseException;
import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.server.domain.UserAuthKey;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.security.SecurityTool;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.web.controller.api.AbstractApiController;
import cz.clovekvtisni.coordinator.util.RunnableWithResult;
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
        UserRequest<LoginRequestParams> req = parseRequestAnonymous(request, LoginRequestParams.class);
        UserEntity user = userService.login(req.params.getLogin(), req.params.getPassword());
        if (user == null) {
            throw MaPermissionDeniedException.wrongCredentials();
        }
        return okResult(new LoginResponseData(user.buildTargetEntity()));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public @ResponseBody ApiResponse register(HttpServletRequest request) {
        UserRequest<RegisterRequestParams> req = parseRequestAnonymous(request, RegisterRequestParams.class);
        User newUser = req.params.getNewUser();
        if (newUser == null)
            throw MaParseException.wrongRequestParams();

        final UserEntity newUserEntity = new UserEntity().populateFrom(newUser);
        newUserEntity.setRoleIdList(new String[] {AuthorizationTool.ANONYMOUS});
        UserInEvent inEvent = req.params.getUserInEvent();

        UserEntity user;
        if (inEvent != null) {
            UserInEventEntity inEventEntity = new UserInEventEntity().populateFrom(inEvent);
            UserInEventEntity regResult = userService.register(newUserEntity, inEventEntity);
            user = regResult.getUserEntity();

        } else {
            user = userService.preRegister(newUserEntity);
        }

        RegisterResponseData responseData = new RegisterResponseData(user.buildTargetEntity());
        UserAuthKey authKey = userService.createAuthKey(user);
        responseData.setAuthKey(authKey.getAuthKey());

        return okResult(responseData);
    }
}
