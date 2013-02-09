package cz.clovekvtisni.coordinator.android.api;

import cz.clovekvtisni.coordinator.api.request.EventPoiListRequestParams;
import cz.clovekvtisni.coordinator.api.response.EventPoiFilterResponseData;

public class EventPoiListCall extends
		ApiCall<EventPoiListRequestParams, EventPoiFilterResponseData> {

	public EventPoiListCall(EventPoiListRequestParams requestParams) {
		super("event/poi/list", requestParams, EventPoiFilterResponseData.class);
	}
	
	public static interface Listener extends ApiCall.Listener<EventPoiFilterResponseData> {
	}

}
