package cz.clovekvtisni.coordinator.android.api;

import cz.clovekvtisni.coordinator.api.request.RegisterRequestParams;
import cz.clovekvtisni.coordinator.api.response.RegisterResponseData;

public class UserRegisterCall extends ApiCall<RegisterRequestParams, RegisterResponseData> {

	public UserRegisterCall(RegisterRequestParams requestParams) {
		super("user/register", requestParams, RegisterResponseData.class);
	}
	
	public static interface Listener extends ApiCall.Listener<RegisterResponseData> {
	}

}
