package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.exception.ErrorCode;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/27/12
 * Time: 1:36 AM
 */
public class ApiResponse<T extends ApiResponseData> {

    private Status status;

    private ErrorCode errorCode;

    private String errorMessage;

    private T data;

    private ApiResponse() {
    }

    public ApiResponse(T data) {
        status = Status.OK;
        this.data = data;
    }

    public ApiResponse(ErrorCode errorCode, String errorMessage) {
        status = Status.ERROR;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Status getStatus() {
        return status;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public T getData() {
        return data;
    }

    public static enum Status {
        OK, ERROR
    }
}
