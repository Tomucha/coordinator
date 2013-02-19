package cz.clovekvtisni.coordinator.android.api;

import cz.clovekvtisni.coordinator.api.request.EventFilterRequestParams;
import cz.clovekvtisni.coordinator.api.response.EventFilterResponseData;

public class EventRegisteredCall extends
		ApiCall<EventFilterRequestParams, EventFilterResponseData> {

	public EventRegisteredCall(EventFilterRequestParams requestParams) {
		super("event/registered", requestParams, EventFilterResponseData.class);
	}

	public static interface Listener extends ApiCall.Listener<EventFilterResponseData> {
	}

}
