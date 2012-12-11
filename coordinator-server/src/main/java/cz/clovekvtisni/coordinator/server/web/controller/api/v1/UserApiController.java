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
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
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
        UserRequest<LoginRequestParams> req = parseRequestAnonymous(request, LoginRequestParams.class);
        UserEntity user = userService.login(req.params.getLogin(), req.params.getPassword());
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
        UserRequest<RegisterRequestParams> req = parseRequestAnonymous(request, RegisterRequestParams.class);
        User newUser = req.params.getNewUser();
        if (newUser == null)
            throw MaParseException.wrongRequestParams();

        final UserEntity newUserEntity = new UserEntity().populateFrom(newUser);

        // this method is only for new users. For existed users will be method /add-to-event
        if (!newUserEntity.isNew())
            throw MaPermissionDeniedException.registrationNotAllowed();

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

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public @ResponseBody ApiResponse filter(HttpServletRequest request) {
        UserRequest<UserListRequestParams> req = parseRequest(request, UserListRequestParams.class);

        UserFilter filter = new UserFilter(true);
        filter.setOrganizationIdVal(req.user.getOrganizationId());
        if (req.params.getModifiedFrom() != null) {
            filter.setModifiedDateVal(req.params.getModifiedFrom());
            filter.setModifiedDateOp(Filter.Operator.GT);
        }
        filter.setOrder("modifiedDate");

        ResultList<UserEntity> result = userService.findByFilter(filter, 0, null, 0l);
        List<User> users = new EntityTool().buildTargetEntities(result.getResult());

        return okResult(new UserFilterResponseData(users));
    }

    @RequestMapping(value = "/by-id", method = RequestMethod.POST)
    public @ResponseBody ApiResponse byId(HttpServletRequest request) {
        UserRequest<UserByIdRequestParams> req = parseRequest(request, UserByIdRequestParams.class);

        if (req.params.getById() == null)
            return okResult(new UserByIdResponseData(new User[0]));

        List<User> result = new EntityTool().buildTargetEntities(userService.findByIds(0l, req.params.getById()));

        return okResult(new UserByIdResponseData(result));
    }

}
