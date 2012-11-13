package cz.clovekvtisni.coordinator.android.api;

import roboguice.inject.ContextSingleton;
import cz.clovekvtisni.coordinator.api.response.GlobalConfigResponse;

/**
 * Abychom nemeli instance {@link ApiCall} rozstrkane vsude po kodu, neni
 * konstruktor {@link ApiCall} verejny a vsechny instance se musi vytvaret zde.
 * 
 * @author tomucha
 */
@ContextSingleton
public class ApiCallFactory {

	public ApiCall<Void, GlobalConfigResponse> globalConfiguration() {
		return new ApiCall<Void, GlobalConfigResponse>("config", "global", GlobalConfigResponse.class);
	}

}