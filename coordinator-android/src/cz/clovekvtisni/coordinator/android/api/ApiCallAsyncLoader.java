package cz.clovekvtisni.coordinator.android.api;

import android.content.Context;
import android.content.Loader;
import android.support.v4.app.LoaderManager;

import com.google.common.base.Objects;

import cz.clovekvtisni.coordinator.android.util.MicroCache;
import cz.clovekvtisni.coordinator.android.util.ThrowableLoader;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.ApiResponse.Status;
import cz.clovekvtisni.coordinator.api.response.ApiResponseData;

/**
 * Propojení našich Api volání se standardním Androidím {@link Loader} a
 * {@link LoaderManager}.
 * 
 * @author tomucha
 * 
 * @param <REQUEST>
 * @param <RESPONSE>
 */
public class ApiCallAsyncLoader<REQUEST, RESPONSE extends ApiResponseData> extends
		ThrowableLoader<RESPONSE> {

	private static final int TIME_TO_LIVE_IN_CACHE = 60 * 3;

	private ApiCall<REQUEST, RESPONSE> apiCall;
	private REQUEST requestData;

	private static MicroCache<CacheKey, Object> cache = new MicroCache<ApiCallAsyncLoader.CacheKey, Object>();

	public ApiCallAsyncLoader(Context context, ApiCall<REQUEST, RESPONSE> apiCall,
			REQUEST requestData) {
		// FIXME: sessionId
		super(context, null);
		this.apiCall = apiCall;
		this.requestData = requestData;
	}

	@Override
	public RESPONSE loadData() throws Exception {
		String authKey = null;

		RESPONSE data = null;

		// FIXME: smazat cache pro refresh a logout
		CacheKey key = new CacheKey(apiCall, requestData);
		data = (RESPONSE) cache.get(key);
		if (data != null) return data;

		ApiResponse<RESPONSE> response = apiCall.doRequest(requestData, authKey);
		if (response.getStatus() != Status.OK) {
			throw new ApiResponseException(response);
		}
		data = response.getData();
		if (data != null) {
			cache.put(key, data, TIME_TO_LIVE_IN_CACHE);
		}
		return data;
	}

	private static class CacheKey {
		private ApiCall apiCall;
		private Object request;

		private CacheKey(ApiCall apiCall, Object request) {
			this.apiCall = apiCall;
			this.request = request;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(apiCall, request);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj == null) return false;

			if (getClass().equals(obj.getClass())) {
				CacheKey other = (CacheKey) obj;
				return Objects.equal(apiCall, other.apiCall)
						&& Objects.equal(request, other.request);
			}

			return false;
		}
	}

}