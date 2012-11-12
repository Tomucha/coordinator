package cz.clovekvtisni.coordinator.android.api;

import android.content.Context;
import android.content.Loader;
import android.support.v4.app.LoaderManager;
import cz.clovekvtisni.coordinator.android.util.MicroCache;
import cz.clovekvtisni.coordinator.android.util.ThrowableLoader;

/**
 * Propojení našich Api volání se standardním Androidím {@link Loader} a {@link LoaderManager}.
 * 
 * @author tomucha
 *
 * @param <REQUEST>
 * @param <RESPONSE>
 */
public class ApiCallAsyncLoader<REQUEST, RESPONSE> extends ThrowableLoader<RESPONSE> {

	private static final int TIME_TO_LIVE_IN_CACHE = 60 * 3;
	
	private ApiCall<REQUEST, RESPONSE> apiCall;
	private REQUEST requestData;
	
	private static MicroCache<CacheKey, Object> cache = new MicroCache<ApiCallAsyncLoader.CacheKey, Object>();

	public ApiCallAsyncLoader(Context context, ApiCall<REQUEST, RESPONSE> apiCall, REQUEST requestData) {
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
		if (!response.isOk()) {
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
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((apiCall == null) ? 0 : apiCall.hashCode());
			result = prime * result
					+ ((request == null) ? 0 : request.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CacheKey other = (CacheKey) obj;
			if (apiCall == null) {
				if (other.apiCall != null)
					return false;
			} else if (!apiCall.equals(other.apiCall))
				return false;
			if (request == null) {
				if (other.request != null)
					return false;
			} else if (!request.equals(other.request))
				return false;
			return true;
		}
	}

}