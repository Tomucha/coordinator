package cz.clovekvtisni.coordinator.server.web.controller.api;

import cz.clovekvtisni.coordinator.api.request.EventRequestParams;
import cz.clovekvtisni.coordinator.api.request.RequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.ApiResponseData;
import cz.clovekvtisni.coordinator.exception.ErrorCode;
import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.exception.MaParseException;
import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import cz.clovekvtisni.coordinator.server.security.SecurityTool;
import cz.clovekvtisni.coordinator.server.security.plugin.UserInEventSecurityPlugin;
import cz.clovekvtisni.coordinator.server.service.UserInEventService;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.util.RunnableWithResult;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 3:48 PM
 */
public abstract class AbstractApiController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected static final String SIGNATURE_PARAMETER = "signature";
    protected static final String AUTHKEY_PARAMETER = "authKey";
    protected static final String REQUEST_PARAMETER = "data";

    private static final String SECRET = "bs~D!er4@#w4#%$2Y3wZšWE#$3";

    protected ObjectMapper objectMapper;


    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserInEventService userInEventService;

    @Autowired
    protected SecurityTool securityTool;

    @Autowired
    protected CoordinatorConfig config;

    @Autowired
    protected AppContext appContext;

    protected <PARAMS extends RequestParams> PARAMS parseRequestAnonymous(HttpServletRequest request, final Class<PARAMS> paramClass) {
        return parseParams(request, true, paramClass);
    }

    protected <PARAMS extends RequestParams> PARAMS parseRequest(HttpServletRequest request, final Class<PARAMS> paramClass) {
        return parseParams(request, false, paramClass);
    }

    protected <PARAMS extends RequestParams> PARAMS parseParams(HttpServletRequest request, boolean isAnonymous, final Class<PARAMS> paramClass) {
        return parseParams(request, isAnonymous, new ParseParamsCallback<PARAMS>() {
            @Override
            public PARAMS parse(JsonParser jsonParser) {
                try {
                    return objectMapper.readValue(jsonParser, paramClass);
                } catch (IOException e) {
                    throw MaParseException.wrongRequestParams();
                } catch (RuntimeException e) {
                    logger.error("Cannot parse params: "+e,e);
                    throw e;
                }
            }
        });
    }

    protected <PARAMS extends RequestParams> PARAMS parseParams(HttpServletRequest request, boolean isAnonymous, ParseParamsCallback<PARAMS> parseCallback) {

        String signature = null;
        PARAMS params = null;
        String token = null;
        String authKey = null;

        try {
            JsonParser jp = objectMapper.getJsonFactory().createJsonParser(request.getInputStream());

            JsonToken current;

            current = jp.nextToken();
            if (current != JsonToken.START_OBJECT) {
                throw new IllegalArgumentException("wrong JSON format, expected start object, received: "+current);
            }
            while (jp.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jp.getCurrentName();
                if (SIGNATURE_PARAMETER.equals(fieldName)) {
                    signature = jp.nextTextValue();
                }
                else if (AUTHKEY_PARAMETER.equals(fieldName)) {
                    authKey = jp.nextTextValue();
                }
                else if (REQUEST_PARAMETER.equals(fieldName)) {
                    jp.nextToken();
                    params = parseCallback.parse(jp);
                }
                else {
                    logger.warn("unexpected field {}", fieldName);
                    jp.nextValue();
                }
            }

            if (!isAnonymous && authKey == null) {
                logger.error("empty auth key");
                throw MaPermissionDeniedException.permissionDenied();

            } else if (authKey != null) {
                appContext.setLoggedUser(userService.getByAuthKey(authKey));
                if (appContext.getLoggedUser() == null) {
                    logger.error("unknown auth key {}", authKey);
                    throw MaPermissionDeniedException.wrongAuthKey();
                }
                if (params != null && params instanceof EventRequestParams) {
                    final Long eventId = ((EventRequestParams) params).getEventId();
                    if (eventId != null) {
                        UserInEventEntity inEvent = securityTool.runWithDisabledSecurity(new RunnableWithResult<UserInEventEntity>() {
                            @Override
                            public UserInEventEntity run() {
                                return userInEventService.findById(eventId, appContext.getLoggedUser().getId(), UserInEventService.FLAG_FETCH_GROUPS);
                            }
                        });
                        appContext.setActiveUserInEvent(inEvent);
                    }
                }
            }

        } catch (IOException e) {
            throw MaParseException.wrongRequestParams();
        }

        return params;
    }

    private String getSecret() {
        return SECRET;
    }

    protected interface ParseParamsCallback<PARAMS extends RequestParams> {
        PARAMS parse(JsonParser jsonParser);
    }

    protected <T extends ApiResponseData> ApiResponse<T> okResult(T data) {
        return new ApiResponse<T>(data);
    }

    protected <T extends ApiResponseData> ApiResponse<T> errorResult(ErrorCode errCode, String errorMessage) {
        return new ApiResponse<T>(errCode, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public @ResponseBody ApiResponse exceptionHandler(Exception ex, HttpServletResponse response) {
        logger.error("Exception: "+ex, ex);
        if (ex instanceof MaException) {
            ErrorCode code = ((MaException) ex).getCode();
            if (code != null) switch (code) {
                case NOT_FOUND: response.setStatus(404); break;
                case PERMISSION_DENIED: response.setStatus(403); break;
                case INTERNAL: response.setStatus(500); break;
            }
            return errorResult(code, ex.getMessage());
        }
        return errorResult(ErrorCode.INTERNAL, ex.getMessage());
    }

    protected UserEntity getLoggedUser() {
        return appContext.getLoggedUser();
    }

}
