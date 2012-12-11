package cz.clovekvtisni.coordinator.android.api;

import cz.clovekvtisni.coordinator.api.request.OrganizationEventsRequestParams;
import cz.clovekvtisni.coordinator.api.response.OrganizationEventsResponseData;

public class OrganizationEventsCall extends
		ApiCall<OrganizationEventsRequestParams, OrganizationEventsResponseData> {

	public OrganizationEventsCall(OrganizationEventsRequestParams requestParams) {
		super("organization", "events", requestParams, OrganizationEventsResponseData.class);
	}
	
	public static interface Listener extends ApiCall.Listener<OrganizationEventsResponseData> {
	}

}
