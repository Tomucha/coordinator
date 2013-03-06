package cz.clovekvtisni.coordinator.android.api;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cz.clovekvtisni.coordinator.android.other.Settings;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.android.util.Utils;
import cz.clovekvtisni.coordinator.api.request.ApiRequest;
import cz.clovekvtisni.coordinator.api.request.RequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponse.Status;
import cz.clovekvtisni.coordinator.api.response.ApiResponseData;

public abstract class ApiCall<RQ extends RequestParams, RP extends ApiResponseData> {

	private static final String API_RESPONSE_STATUS = "status";
	private static final String API_RESPONSE_DATA = "data";
	private static final String URL_PREFIX = "https://coordinator-test.appspot.com/api/v1/";

	private final Class<? extends RP> responseClass;
	private final RQ requestParams;
	private final String url;
	private final String requestBody;

	public ApiCall(String urlSuffix, RQ requestParams, Class<? extends RP> responseClass) {
		this.url = URL_PREFIX + urlSuffix;
		this.requestParams = requestParams;
		this.responseClass = responseClass;
		this.requestBody = createRequestBody();
	}
	
	public RP call() throws ApiCallException {
		try {
			return doRequest();
		} catch (JsonSyntaxException e) {
			throw new ApiCallException(e);
		} catch (HttpRequestException e) {
			throw new ApiCallException(e);
		} catch (ApiServerSideException e) {
			throw new ApiCallException(e);
		}
	}

	private String createRequestBody() {
		ApiRequest request = new ApiRequest();
		String authKey = Settings.getAuthKey();
		if (authKey != null) request.setAuthKey(authKey);
		request.setData(requestParams);
		return ApiUtils.GSON.toJsonTree(request).toString();
	}

	private RP doRequest() throws HttpRequestException, JsonSyntaxException, ApiServerSideException {
		String responseBody = HttpRequest.post(url).send(requestBody).body();
		JsonObject json = (JsonObject) ApiUtils.PARSER.parse(responseBody);

		writeToLog(requestBody, responseBody);

		Status status = ApiUtils.GSON.fromJson(json.get(API_RESPONSE_STATUS), Status.class);
		if (status == Status.OK) {
			JsonElement resultJson = json.get(API_RESPONSE_DATA);
			return ApiUtils.GSON.fromJson(resultJson, responseClass);
		} else {
			throw new ApiServerSideException();
		}
	}
	
	protected String getCacheKey() {
		return Utils.md5(requestBody);
	}

	public RQ getRequestParams() {
		return requestParams;
	}

	public Class<? extends RP> getResponseClass() {
		return responseClass;
	}

	public boolean isEquivalentTo(ApiCall<?, ?> other) {
		return getClass().equals(other.getClass()) && requestBody.equals(other.requestBody);
	}
	
	private void writeToLog(String request, String response) {
		Lg.API.d("ApiCall: " + url);
		Lg.API.dd("Request body:");
		Lg.API.dd(request);
		Lg.API.dd("Response body:");
		Lg.API.dd(response);
	}

	@SuppressWarnings("serial")
	public static class ApiServerSideException extends Exception {
	}

	@SuppressWarnings("serial")
	public static class ApiCallException extends Exception {
		public ApiCallException(Throwable cause) {
			super(cause);
		}
	}

}
