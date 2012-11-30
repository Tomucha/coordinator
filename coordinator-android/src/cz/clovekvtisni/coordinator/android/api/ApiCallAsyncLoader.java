package cz.clovekvtisni.coordinator.android.api;

import android.content.Context;
import cz.clovekvtisni.coordinator.android.util.ThrowableLoader;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.ApiResponse.Status;
import cz.clovekvtisni.coordinator.api.response.ApiResponseData;

public class ApiCallAsyncLoader<REQUEST, RESPONSE extends ApiResponseData> extends
		ThrowableLoader<RESPONSE> {
	private ApiCall<REQUEST, RESPONSE> apiCall;
	private REQUEST requestData;

	public ApiCallAsyncLoader(Context context, ApiCall<REQUEST, RESPONSE> apiCall,
			REQUEST requestData) {
		super(context, null);
		this.apiCall = apiCall;
		this.requestData = requestData;
	}

	@Override
	public RESPONSE loadData() throws Exception {
		String authKey = null;

		ApiResponse<RESPONSE> response = apiCall.doRequest(requestData, authKey);
		if (response.getStatus() != Status.OK) {
			throw new ApiResponseException(response);
		}
		return response.getData();
	}

}