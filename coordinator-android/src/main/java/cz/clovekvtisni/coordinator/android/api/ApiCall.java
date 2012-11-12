package cz.clovekvtisni.coordinator.android.api;

import java.net.HttpURLConnection;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.JsonObject;

import cz.clovekvtisni.coordinator.android.DeployEnvironment;
import cz.clovekvtisni.coordinator.android.util.CommonTool;
import cz.clovekvtisni.coordinator.android.util.GsonTool;
import cz.clovekvtisni.coordinator.api.request.ApiRequest;

/**
 * Tohle je jedno HTTP / JSON / API volani.
 * Vezme objekt, zabali do Json obalky, POSTne na server, ziska odpoved a odevzda jako {@link ApiResponse}.
 * Je nutne spustet ho asychronne, aby neblokovalo UI.
 * 
 * Pouziva https://github.com/kevinsawicki/http-request wrapper nad {@link HttpURLConnection}.
 * 
 * Nevytvarejte instance teto tridy, pouzivejte {@link ApiCallFactory}, ne ze by to necemu vadilo, ale vytvari to bordel v kodu.
 * 
 * @author tomucha
 */
public final class ApiCall<REQUEST, RESPONSE> {

	public static final String SERVER = CommonTool.getEnvironment().getApiHost();
	public static final int PORT = CommonTool.getEnvironment().getApiPort();
	public static final String API_VERSION = CommonTool.getEnvironment().getApiVersion();
	public static final String HTTP_BASE = "https://"+ApiCall.SERVER+":"+ApiCall.PORT;
	
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
		apiUrl = HTTP_BASE+"/api/"+API_VERSION+"/"+entity+"/"+operation;
		this.responseDataType = responseDataType;
	}
	
	public ApiResponse<RESPONSE> doRequest(REQUEST requestData, String authKey) {
		
		// FIXME: sessionId, token apod.
		ApiRequest request = new ApiRequest();
		request.setData(requestData);
		
		CommonTool.logI(getClass().getSimpleName(), "Calling API: "+apiUrl);
		
		String requestBody = GsonTool.toJson(request).toString();
		String responseBody = HttpRequest.post(apiUrl).send(requestBody).body();
		JsonObject responseJson = (JsonObject) GsonTool.parse(responseBody);
		CommonTool.logI(getClass().getSimpleName(), "API response: "+responseJson);
		return new ApiResponse<RESPONSE>(responseJson, responseDataType);
	}
	
	@Override
	public String toString() {
		return "ApiCall [" + apiUrl + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((apiUrl == null) ? 0 : apiUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApiCall other = (ApiCall) obj;
		if (apiUrl == null) {
			if (other.apiUrl != null)
				return false;
		} else if (!apiUrl.equals(other.apiUrl))
			return false;
		return true;
	}
	
}