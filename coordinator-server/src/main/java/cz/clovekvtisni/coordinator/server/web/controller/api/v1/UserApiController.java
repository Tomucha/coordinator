package cz.clovekvtisni.coordinator.server.web.controller.api.v1;

import cz.clovekvtisni.coordinator.api.request.*;
import cz.clovekvtisni.coordinator.api.response.*;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.exception.MaParseException;
import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.server.domain.UserAuthKey;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.util.EntityTool;
import cz.clovekvtisni.coordinator.server.web.controller.api.AbstractApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        LoginRequestParams params = parseRequestAnonymous(request, LoginRequestParams.class);
        UserEntity user = userService.loginApi(params.getLogin(), params.getPassword());
        if (user == null) {
            throw MaPermissionDeniedException.wrongCredentials();
        }
        LoginResponseData responseData = new LoginResponseData(user.buildTargetEntity());
        UserAuthKey authKey = userService.createAuthKey(user);
        responseData.setAuthKey(authKey.getAuthKey());
        return okResult(responseData);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public @ResponseBody ApiResponse register(HttpServletRequest request) {
        RegisterRequestParams params = parseRequestAnonymous(request, RegisterRequestParams.class);
        User newUser = params.getNewUser();
        if (newUser == null)
            throw MaParseException.wrongRequestParams();

        final UserEntity newUserEntity = new UserEntity().populateFrom(newUser);

        // this method is only for new users. For existed users will be method /add-to-event

        UserEntity loggedUser = getLoggedUser();

        if (loggedUser == null) {
            newUserEntity.setRoleIdList(new String[] {AuthorizationTool.ANONYMOUS});
        } else {
            // it's not new
            newUserEntity.setId(loggedUser.getId());
        }
        UserInEvent inEvent = params.getUserInEvent();

        UserEntity user;
        if (inEvent != null) {
            UserInEventEntity inEventEntity = new UserInEventEntity().populateFrom(inEvent);
            UserInEventEntity regResult = userService.register(newUserEntity, inEventEntity, 0l);
            user = regResult.getUserEntity();

        } else {
            user = userService.preRegister(newUserEntity, 0l, true);
        }

        RegisterResponseData responseData = new RegisterResponseData(user.buildTargetEntity());

        if (loggedUser == null) {
            UserAuthKey authKey = userService.createAuthKey(user);
            responseData.setAuthKey(authKey.getAuthKey());
        }

        return okResult(responseData);
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public @ResponseBody ApiResponse filter(HttpServletRequest request) {
        UserListRequestParams params = parseRequest(request, UserListRequestParams.class);

        UserFilter filter = new UserFilter(true);
        filter.setOrganizationIdVal(getLoggedUser().getOrganizationId());
        if (params.getModifiedFrom() != null) {
            filter.setModifiedDateVal(params.getModifiedFrom());
            filter.setModifiedDateOp(Filter.Operator.GT);
        }
        filter.setOrder("modifiedDate");

        ResultList<UserEntity> result = userService.findByFilter(filter, 0, null, 0l);
        List<User> users = new EntityTool().buildTargetEntities(result.getResult());

        return okResult(new UserFilterResponseData(users));
    }

    @RequestMapping(value = "/myself", method = RequestMethod.POST)
    public @ResponseBody ApiResponse myself(HttpServletRequest request) {
        parseRequest(request, EmptyRequestParams.class);
        UserEntity logged = userService.findById(getLoggedUser().getId(), UserService.FLAG_FETCH_EQUIPMENT | UserService.FLAG_FETCH_SKILLS);
        User u = logged.buildTargetEntity();
        return okResult(new UserByIdResponseData(u));
    }

    @RequestMapping(value = "/by-id", method = RequestMethod.POST)
    public @ResponseBody ApiResponse byId(HttpServletRequest request) {
        UserByIdRequestParams params = parseRequest(request, UserByIdRequestParams.class);

        if (params.getById() == null)
            return okResult(new UserByIdResponseData(new User[0]));

        List<User> result = new EntityTool().buildTargetEntities(userService.findByIds(0l, params.getById()));

        return okResult(new UserByIdResponseData(result));
    }

    @RequestMapping(value = "/register-push-token-android", method = RequestMethod.POST)
    public @ResponseBody ApiResponse registerPushTokenAndroid(HttpServletRequest request) {
        UserPushTokenRequestParams params = parseRequest(request, UserPushTokenRequestParams.class);
        userService.registerPushTokenAndroid(params.getToken());
        return okResult(null);
    }

}
