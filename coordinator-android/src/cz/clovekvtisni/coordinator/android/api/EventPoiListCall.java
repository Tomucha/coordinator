package cz.clovekvtisni.coordinator.android.api;

import cz.clovekvtisni.coordinator.api.request.OrganizationEventsRequestParams;
import cz.clovekvtisni.coordinator.api.response.OrganizationEventsResponseData;

public class EventPoiListCall extends
		ApiCall<OrganizationEventsRequestParams, OrganizationEventsResponseData> {

	public EventPoiListCall(OrganizationEventsRequestParams requestParams) {
		super("organization/events", requestParams, OrganizationEventsResponseData.class);
	}
	
	public static interface Listener extends ApiCall.Listener<OrganizationEventsResponseData> {
	}

}
