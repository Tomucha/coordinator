package cz.clovekvtisni.coordinator.server.web.controller.api;

import cz.clovekvtisni.coordinator.api.request.RequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.ApiResponseData;
import cz.clovekvtisni.coordinator.exception.ErrorCode;
import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.exception.MaParseException;
import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.util.SignatureTool;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
    protected static final String TOKEN_PARAMETER = "token";
    protected static final String SESSION_PARAMETER = "sessionId";
    protected static final String REQUEST_PARAMETER = "data";

    private static final String SECRET = "bs~D!er4@#w4#%$2Y3wZšWE#$3";

    protected ObjectMapper objectMapper;


    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected <PARAMS extends RequestParams> PARAMS parseParams(HttpServletRequest request, final Class<PARAMS> paramClass) {
        return parseParams(request, new ParseParamsCallback<PARAMS>() {
            @Override
            public PARAMS parse(JsonParser jsonParser) {
                try {
                    return objectMapper.readValue(jsonParser, paramClass);
                } catch (IOException e) {
                    throw MaParseException.wrongRequestParams();
                }
            }
        });
    }

    protected <PARAMS extends RequestParams> PARAMS parseParams(HttpServletRequest request, ParseParamsCallback<PARAMS> parseCallback) {

        String signature = null;
        PARAMS params = null;
        String token = null;
        String sessionId = null;

        try {
            JsonParser jp = objectMapper.getJsonFactory().createJsonParser(request.getInputStream());

            JsonToken current;

            current = jp.nextToken();
            if (current != JsonToken.START_OBJECT) {
                throw new IllegalArgumentException("wrong JSON format");
            }
            while (jp.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jp.getCurrentName();
                if (SIGNATURE_PARAMETER.equals(fieldName)) {
                    signature = jp.nextTextValue();
                }
                else if (TOKEN_PARAMETER.equals(fieldName)) {
                    token = jp.nextTextValue();
                }
                else if (SESSION_PARAMETER.equals(fieldName)) {
                    sessionId = jp.nextTextValue();
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

            //TODO: kontrola tokenov? alebo to budeme zabezpecovat inaksie?
            if (token != null) {
    /*
                CacheManager cacheManager = CacheManager.getInstance();
                if (!cacheManager.putUnusedApiToken(token)) {
                    throw MaPermissionDeniedException.tokenAlreadyUsed(token);
                }
    */
            }

            String signatureComputed = SignatureTool.sign(SignatureTool.computeHash(params), getSecret());
            if (signature == null || !signature.equalsIgnoreCase(signatureComputed)) {
                logger.info("wrong signature '{}', correct '{}'", signature, signatureComputed);
                throw MaPermissionDeniedException.wrongSignature(signature, "json");
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
    public @ResponseBody ApiResponse exceptionHandler(Exception ex) {
        if (ex instanceof MaException) {
            return errorResult(((MaException) ex).getCode(), ex.getMessage());
        }
        return errorResult(ErrorCode.INTERNAL, ex.getMessage());
    }
}
