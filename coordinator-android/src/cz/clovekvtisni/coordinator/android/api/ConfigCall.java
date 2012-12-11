package cz.clovekvtisni.coordinator.android.api;

import cz.clovekvtisni.coordinator.api.request.EmptyRequestParams;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;

public class ConfigCall extends ApiCall<EmptyRequestParams, ConfigResponse> {

	public ConfigCall() {
		super("config", "", new EmptyRequestParams(), ConfigResponse.class);
	}
	
	public static interface Listener extends ApiCall.Listener<ConfigResponse> {
	}

}
