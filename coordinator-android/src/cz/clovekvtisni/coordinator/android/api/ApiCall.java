package cz.clovekvtisni.coordinator.android.api;

import java.net.HttpURLConnection;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Objects;
import com.google.gson.JsonObject;

import cz.clovekvtisni.coordinator.android.DeployEnvironment;
import cz.clovekvtisni.coordinator.android.util.CommonTool;
import cz.clovekvtisni.coordinator.android.util.GsonTool;
import cz.clovekvtisni.coordinator.api.request.ApiRequest;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.ApiResponse.Status;
import cz.clovekvtisni.coordinator.api.response.ApiResponseData;
import cz.clovekvtisni.coordinator.exception.ErrorCode;

/**
 * Tohle je jedno HTTP / JSON / API volani. Vezme objekt, zabali do Json obalky,
 * POSTne na server, ziska odpoved a odevzda jako {@link ApiResponse}. Je nutne
 * spustet ho asychronne, aby neblokovalo UI.
 * 
 * Pouziva https://github.com/kevinsawicki/http-request wrapper nad
 * {@link HttpURLConnection}.
 * 
 * Nevytvarejte instance teto tridy, pouzivejte {@link ApiCallFactory}, ne ze by
 * to necemu vadilo, ale vytvari to bordel v kodu.
 * 
 * @author tomucha
 */
public final class ApiCall<REQUEST, RESPONSE extends ApiResponseData> {

	public static final String SERVER = CommonTool.getEnvironment().getApiHost();
	public static final int PORT = CommonTool.getEnvironment().getApiPort();
	public static final String API_VERSION = CommonTool.getEnvironment().getApiVersion();
	public static final String HTTP_BASE = "https://" + ApiCall.SERVER + ":" + ApiCall.PORT;

	private String apiUrl;
	private Class<? extends RESPONSE> responseDataType;

	/**
	 * Vygeneruje adresu: http://host:port/metis/api/VERSION/ENTITY/OPERATION
	 * 
	 * Host a port se bere z {@link DeployEnvironment}.
	 * 
	 * @param entity
	 * @param operation
	 * @param responseDataType
	 */
	ApiCall(final String entity, final String operation, Class<? extends RESPONSE> responseDataType) {
		apiUrl = HTTP_BASE + "/api/" + API_VERSION + "/" + entity + "/" + operation;
		this.responseDataType = responseDataType;
	}

	/**
	 * Method builds {@link ApiRequest}, executes {@link HttpRequest} and
	 * returns {@link ApiResponse}.
	 * 
	 * MUST NOT be called from UI thread, communicates over the network.
	 * 
	 * @param requestData
	 * @param authKey
	 * @return
	 */
	public ApiResponse<RESPONSE> doRequest(REQUEST requestData, String authKey) {
		// FIXME: sessionId, token apod.
		ApiRequest request = new ApiRequest();
		request.setData(requestData);

		CommonTool.logI(getClass().getSimpleName(), "Calling API: " + apiUrl);

		String requestBody = GsonTool.toJson(request).toString();
		String responseBody = HttpRequest.post(apiUrl).send(requestBody).body();
		JsonObject responseJson = (JsonObject) GsonTool.parse(responseBody);
		CommonTool.logI(getClass().getSimpleName(), "API response: " + responseJson);

		return buildApiResponse(responseJson);
	}

	/**
	 * This method takes JSON response and create an {@link ApiResponse} object,
	 * depending on obtained response {@link Status}.
	 * 
	 * @param responseJson
	 * @return
	 */
	private ApiResponse<RESPONSE> buildApiResponse(JsonObject responseJson) {
		ApiResponse<RESPONSE> response = null;
		Status status = GsonTool.fromJson(responseJson.get(ApiConstants.API_RESPONSE_STATUS),
				Status.class);
		if (status == Status.OK) {
			// everything ok, let's parse response data
			return new ApiResponse<RESPONSE>(GsonTool.fromJson(
					responseJson.get(ApiConstants.API_RESPONSE_DATA), responseDataType));
		} else {
			// something is wrong, return error response
			ErrorCode code = GsonTool.fromJson(
					responseJson.get(ApiConstants.API_RESPONSE_ERROR_CODE), ErrorCode.class);
			String message = null;
			if (responseJson.has(ApiConstants.API_RESPONSE_ERROR_MESSAGE)) {
				message = responseJson.get(ApiConstants.API_RESPONSE_ERROR_MESSAGE).getAsString();
			}
			return new ApiResponse<RESPONSE>(code, message);
		}
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).addValue(apiUrl).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(apiUrl);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;

		if (getClass().equals(obj.getClass())) {
			final ApiCall<?, ?> other = (ApiCall<?, ?>) obj;
			return Objects.equal(apiUrl, other.apiUrl);
		}

		return false;
	}

}