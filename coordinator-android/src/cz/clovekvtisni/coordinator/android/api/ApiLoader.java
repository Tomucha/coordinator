package cz.clovekvtisni.coordinator.android.api;

import com.fhucho.android.workers.Loader;

import cz.clovekvtisni.coordinator.android.api.ApiCache.Item;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.api.request.RequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponseData;

public abstract class ApiLoader<RQ extends RequestParams, RP extends ApiResponseData> extends
		Loader<ApiLoader.Listener<RP>> {

	private final ApiCall<RQ, RP> apiCall;

	private volatile Result result;

	public ApiLoader(ApiCall<RQ, RP> apiCall, Class<? extends ApiLoader.Listener<RP>> typeOfListener) {
		super(typeOfListener);
		this.apiCall = apiCall;
	}
	
	// FIXME
	private volatile boolean reload = false;

	@Override
	protected void doInBackground() {
		try {
			ApiCache cache = ApiCache.getInstance();

			Item<RP> item = cache.get(apiCall.getCacheKey(), apiCall.getResponseClass());
			if (reload) item = null; // FIXME

			if (item != null) {
				Lg.API_LOADER.d("Cached value exists, loading.");
				result = new Result(item.getValue());
				result.sendToListener();
			}

			if (item == null || (System.currentTimeMillis() - item.getTime() > 5 * 60 * 1000)) {
				Lg.API_LOADER.d("Doing api call.");
				result = new Result(apiCall.call());
				cache.put(apiCall.getCacheKey(), result.response);
				result.sendToListener();
			}
		} catch (Exception e) {
			Lg.API_LOADER.w("Exception while loading", e);
			new Result(e).sendToListener();
		}
	}

	@Override
	protected boolean isEquivalentTo(Loader<?> other) {
		if (getClass().equals(other.getClass())) {
			ApiLoader<?, ?> otherApiCallLoader = (ApiLoader<?, ?>) other;
			if (apiCall.isEquivalentTo(otherApiCallLoader.apiCall)) return true;
		}
		return false;
	}

	@Override
	protected void onListenerAdded() {
		if (result != null) result.sendToListener();
	}

	public void reload() {
		reload = true;
		new Thread() {
			public void run() {
				doInBackground();
			};
		}.start();
	}

	private class Result {
		private final Exception exception;
		private final RP response;

		private Result(Exception exp) {
			exception = exp;
			response = null;
		}

		private Result(RP resp) {
			response = resp;
			exception = null;
		}

		private void sendToListener() {
			if (response != null) getListenerProxy().onResult(response);
			if (exception != null) getListenerProxy().onException(exception);
		}
	}

	public static interface Listener<RP> {
		public void onResult(RP result);

		public void onException(Exception e);
	}

}
