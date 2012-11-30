package cz.clovekvtisni.coordinator.android.api;

import cz.clovekvtisni.coordinator.api.request.EmptyRequestParams;
import cz.clovekvtisni.coordinator.api.request.OrganizationEventsRequestParams;
import cz.clovekvtisni.coordinator.api.request.RegisterRequestParams;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
import cz.clovekvtisni.coordinator.api.response.OrganizationEventsResponseData;
import cz.clovekvtisni.coordinator.api.response.RegisterResponseData;

/**
 * Abychom nemeli instance {@link ApiCall} rozstrkane vsude po kodu, neni
 * konstruktor {@link ApiCall} verejny a vsechny instance se musi vytvaret zde.
 * 
 * @author tomucha
 */
public class ApiCallFactory {

	public static ApiCall<EmptyRequestParams, ConfigResponse> configuration() {
		return new ApiCall<EmptyRequestParams, ConfigResponse>("config", "", ConfigResponse.class);
	}

	public static ApiCall<OrganizationEventsRequestParams, OrganizationEventsResponseData> organizationEvents() {
		return new ApiCall<OrganizationEventsRequestParams, OrganizationEventsResponseData>(
				"organization", "events", OrganizationEventsResponseData.class);
	}

	public static ApiCall<RegisterRequestParams, RegisterResponseData> register() {
		return new ApiCall<RegisterRequestParams, RegisterResponseData>("user", "register",
				RegisterResponseData.class);
	}

}