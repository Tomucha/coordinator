package cz.clovekvtisni.coordinator.android.api;

import com.google.gson.JsonObject;

import cz.clovekvtisni.coordinator.android.util.CommonTool;
import cz.clovekvtisni.coordinator.android.util.GsonTool;

/**
 * Objekt, ktery zabaluje vysledek API volani.
 * 
 * Predame ji ziskany JSON, vysvetlime ji, jakeho typu je vysledek volani a ona nam z toho udela pekny Java objekt.
 * 
 * @author tomucha
 */
public class ApiResponse<DATATYPE> {

	private String entity;
	private String operation;
	private DATATYPE data;
	private String message;
	private String exception;
	private String version;
	private ApiResponseStatus status = ApiResponseStatus.ERROR;

	public ApiResponse(JsonObject object, Class<? extends DATATYPE> dataTypeClass) {
		if (object.has(ApiConstants.API_RESPONSE_ENTITY)) {
			entity = object.get(ApiConstants.API_RESPONSE_ENTITY).getAsString();
		}
		if (object.has(ApiConstants.API_RESPONSE_OPERATION)) {
			operation = object.get(ApiConstants.API_RESPONSE_OPERATION).getAsString();
		}
		if (object.has(ApiConstants.API_RESPONSE_EXCEPTION)) {
			exception = object.get(ApiConstants.API_RESPONSE_EXCEPTION).getAsString();
		}
		if (object.has(ApiConstants.API_RESPONSE_MESSAGE)) {
			message = object.get(ApiConstants.API_RESPONSE_MESSAGE).getAsString();
		}
		if (object.has(ApiConstants.API_RESPONSE_VERSION)) {
			version = object.get(ApiConstants.API_RESPONSE_VERSION).getAsString();
		}
		if (object.has(ApiConstants.API_RESPONSE_STATUS)) {
			try {
				status = ApiResponseStatus.valueOf(object.get(ApiConstants.API_RESPONSE_STATUS).getAsString());
			} catch (Exception e) {
				CommonTool.logE("Cannot resolve respone status", e);
				status = ApiResponseStatus.ERROR;
			}
		}
		if (object.has(ApiConstants.API_RESPONSE_DATA)) {
			data = (DATATYPE) GsonTool.fromJson(object.get(ApiConstants.API_RESPONSE_DATA), dataTypeClass);
		}
	}

	public DATATYPE getData() {
		return data;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ApiResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ApiResponseStatus status) {
		this.status = status;
	}

	public boolean isOk() {
		return status == ApiResponseStatus.OK;
	}

	@Override
	public String toString() {
		return "ApiResponse [entity=" + entity + ", operation=" + operation
				+ ", data=" + data + ", message=" + message + ", exception="
				+ exception + ", version=" + version + ", status=" + status
				+ "]";
	}

}