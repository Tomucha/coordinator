package cz.clovekvtisni.coordinator.android.api;

import cz.clovekvtisni.coordinator.api.request.*;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
import cz.clovekvtisni.coordinator.api.response.EmptyResponseData;
import cz.clovekvtisni.coordinator.api.response.EventFilterResponseData;
import cz.clovekvtisni.coordinator.api.response.EventPoiFilterResponseData;
import cz.clovekvtisni.coordinator.api.response.EventPoiResponseData;
import cz.clovekvtisni.coordinator.api.response.EventUserListResponseData;
import cz.clovekvtisni.coordinator.api.response.RegisterResponseData;
import cz.clovekvtisni.coordinator.api.response.UserUpdatePositionResponseData;

public class ApiCalls {

	public static class ConfigCall extends ApiCall<EmptyRequestParams, ConfigResponse> {
		public ConfigCall() {
			super("config", new EmptyRequestParams(), ConfigResponse.class);
		}
	}

	public static class EventPoiListCall extends
			ApiCall<EventPoiListRequestParams, EventPoiFilterResponseData> {
		public EventPoiListCall(EventPoiListRequestParams requestParams) {
			super("event/poi/list", requestParams, EventPoiFilterResponseData.class);
		}
	}

	public static class EventPoiTransitionCall extends
			ApiCall<EventPoiTransitionRequestParams, EventPoiResponseData> {
		public EventPoiTransitionCall(EventPoiTransitionRequestParams requestParams) {
			super("event/poi/transition", requestParams, EventPoiResponseData.class);
		}
	}

    public static class EventPoiCreateCall extends
            ApiCall<EventPoiCreateRequestParams, EventPoiResponseData> {
        public EventPoiCreateCall(EventPoiCreateRequestParams requestParams) {
            super("event/poi/create", requestParams, EventPoiResponseData.class);
        }
    }

	public static class EventRegisteredCall extends
			ApiCall<EventFilterRequestParams, EventFilterResponseData> {
		public EventRegisteredCall(EventFilterRequestParams requestParams) {
			super("event/registered", requestParams, EventFilterResponseData.class);
		}
	}

	public static class EventUserListCall extends
			ApiCall<EventUserListRequestParams, EventUserListResponseData> {
		public EventUserListCall(EventUserListRequestParams requestParams) {
			super("event/user/list", requestParams, EventUserListResponseData.class);
		}
	}

	public static class UserRegisterCall extends
			ApiCall<RegisterRequestParams, RegisterResponseData> {
		public UserRegisterCall(RegisterRequestParams requestParams) {
			super("user/register", requestParams, RegisterResponseData.class);
		}
	}

	public static class UserUpdatePositionCall extends
			ApiCall<UserUpdatePositionRequestParams, UserUpdatePositionResponseData> {
		public UserUpdatePositionCall(UserUpdatePositionRequestParams requestParams) {
			super("event/user/update-position", requestParams, UserUpdatePositionResponseData.class);
		}
	}

	public static class UserPushTokenCall extends
			ApiCall<UserPushTokenRequestParams, EmptyResponseData> {
		public UserPushTokenCall(UserPushTokenRequestParams requestParams) {
			super("event/user/register-push-token-android", requestParams, EmptyResponseData.class);
		}
	}

}
