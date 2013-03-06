package cz.clovekvtisni.coordinator.android.api;

import cz.clovekvtisni.coordinator.android.api.ApiCalls.ConfigCall;
import cz.clovekvtisni.coordinator.android.api.ApiCalls.EventPoiListCall;
import cz.clovekvtisni.coordinator.android.api.ApiCalls.EventRegisteredCall;
import cz.clovekvtisni.coordinator.android.api.ApiCalls.EventUserListCall;
import cz.clovekvtisni.coordinator.api.request.EmptyRequestParams;
import cz.clovekvtisni.coordinator.api.request.EventFilterRequestParams;
import cz.clovekvtisni.coordinator.api.request.EventPoiListRequestParams;
import cz.clovekvtisni.coordinator.api.request.EventUserListRequestParams;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
import cz.clovekvtisni.coordinator.api.response.EventFilterResponseData;
import cz.clovekvtisni.coordinator.api.response.EventPoiFilterResponseData;
import cz.clovekvtisni.coordinator.api.response.EventUserListResponseData;

public class ApiLoaders {

	public static class ConfigLoader extends ApiLoader<EmptyRequestParams, ConfigResponse> {
		public ConfigLoader() {
			super(new ConfigCall(), ConfigLoaderListener.class);
		}
	}

	public static interface ConfigLoaderListener extends ApiLoader.Listener<ConfigResponse> {
	}

	public static class EventRegisteredLoader extends
			ApiLoader<EventFilterRequestParams, EventFilterResponseData> {
		public EventRegisteredLoader(EventFilterRequestParams params) {
			super(new EventRegisteredCall(params), EventRegisteredLoaderListener.class);
		}
	}

	public static interface EventRegisteredLoaderListener extends
			ApiLoader.Listener<EventFilterResponseData> {
	}

	public static class EventPoiListLoader extends
			ApiLoader<EventPoiListRequestParams, EventPoiFilterResponseData> {
		public EventPoiListLoader(EventPoiListRequestParams params) {
			super(new EventPoiListCall(params), EventPoiListLoaderListener.class);
		}
	}

	public static interface EventPoiListLoaderListener extends
			ApiLoader.Listener<EventPoiFilterResponseData> {
	}

	public static class EventUserListLoader extends
			ApiLoader<EventUserListRequestParams, EventUserListResponseData> {
		public EventUserListLoader(EventUserListRequestParams params) {
			super(new EventUserListCall(params), EventUserListLoaderListener.class);
		}
	}

	public static interface EventUserListLoaderListener extends
			ApiLoader.Listener<EventUserListResponseData> {
	}
}
