package cz.clovekvtisni.coordinator.android.api;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import cz.clovekvtisni.coordinator.android.other.Settings;
import cz.clovekvtisni.coordinator.android.workers.Worker;
import cz.clovekvtisni.coordinator.api.request.ApiRequest;
import cz.clovekvtisni.coordinator.api.request.RequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponse.Status;
import cz.clovekvtisni.coordinator.api.response.ApiResponseData;

public class ApiCall<S extends RequestParams, T extends ApiResponseData> extends
		Worker<ApiCall.Listener<T>> {

	private static final String API_RESPONSE_STATUS = "status";
	private static final String API_RESPONSE_DATA = "data";
	private static final String URL_PREFIX = "https://coordinator-test.appspot.com/api/v1/";

	private final S requestParams;
	private final Class<? extends T> resultClass;
	private final String url;

	public ApiCall(String urlSuffix, S requestParams, Class<? extends T> resultClass) {
		this.url = URL_PREFIX + urlSuffix;
		this.requestParams = requestParams;
		this.resultClass = resultClass;
	}

	private String createRequestBody() {
		ApiRequest request = new ApiRequest();
		String authKey = Settings.getAuthKey();
		if (authKey != null) request.setAuthKey(authKey);
		request.setData(requestParams);
		return ApiUtils.GSON.toJsonTree(request).toString();
	}

	private void doRequest() throws HttpRequestException, JsonSyntaxException, ApiResponseException {
		System.out.println(createRequestBody());
		System.out.println("---------------");
		String responseBody = HttpRequest.post(url).send(createRequestBody()).body();
		JsonObject json = (JsonObject) ApiUtils.PARSER.parse(responseBody);
		System.out.println(json);

		Status status = ApiUtils.GSON.fromJson(json.get(API_RESPONSE_STATUS), Status.class);
		if (status == Status.OK) {
			JsonElement resultJson = json.get(API_RESPONSE_DATA);
			T result = ApiUtils.GSON.fromJson(resultJson, resultClass);
			sendSuccess(result);
		} else {
			throw new ApiResponseException();
		}
	}

	@Override
	protected void doInBackground() {
		try {
			doRequest();
		} catch (HttpRequestException e) {
			sendException(e);
		} catch (ApiResponseException e) {
			sendException(e);
		} catch (JsonParseException e) {
			sendException(e);
		}
	}

	protected void sendException(final Exception e) {
		send(new Runnable() {
			@Override
			public void run() {
				getListener().onException(e);
			}
		});
	}

	protected void sendSuccess(final T result) {
		send(new Runnable() {
			@Override
			public void run() {
				getListener().onResult(result);
			}
		});
	}

	public static interface Listener<T> {
		public void onResult(T result);

		public void onException(Exception e);
	}

	@SuppressWarnings("serial")
	public static class ApiResponseException extends Exception {
	}

}
