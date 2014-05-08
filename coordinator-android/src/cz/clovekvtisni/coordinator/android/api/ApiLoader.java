package cz.clovekvtisni.coordinator.android.api;

import com.fhucho.android.workers.Loader;

import cz.clovekvtisni.coordinator.android.api.ApiCache.Item;
import cz.clovekvtisni.coordinator.android.other.Settings;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.android.util.Utils;
import cz.clovekvtisni.coordinator.api.request.RequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponseData;
import cz.clovekvtisni.coordinator.exception.ErrorCode;

import java.net.SocketException;

public abstract class ApiLoader<RQ extends RequestParams, RP extends ApiResponseData> extends
		Loader<ApiLoader.Listener<RP>> {

	private final ApiCall<RQ, RP> apiCall;

	private volatile Result result;

	public ApiLoader(ApiCall<RQ, RP> apiCall, Class<? extends ApiLoader.Listener<RP>> typeOfListener) {
		super(typeOfListener);
		this.apiCall = apiCall;
	}

	@Override
	protected synchronized void doInBackground(boolean reload) {
		Lg.API_LOADER.i("Starting loader "+this+", reload="+reload);

		ApiCache cache = ApiCache.getInstance();
        Item<RP> item = null;
		if (!reload) {
			item = cache.get(apiCall.getCacheKey(), apiCall.getResponseClass());
		}

        boolean tooOld = false;
		try {

			if (item != null && !reload ) {
				Lg.API_LOADER.d("Cached value exists, loading from cache");
				result = new Result(item.getValue());
				result.sendToListener();
			}
            if (item != null) {
                tooOld = (System.currentTimeMillis() - item.getTime() > 5 * 60 * 1000);
            }
			if (item == null || tooOld || reload) {
				Lg.API_LOADER.d("Doing api call");
				result = new Result(apiCall.call());
				cache.put(apiCall.getCacheKey(), result.response);
				result.sendToListener();
			}
		} catch (Exception e) {
			Lg.API_LOADER.w("Exception while loading", e);

            if (reload) {
                // well, it is an exception, let's notify user, and try to return cache (if reloading)
                item = cache.get(apiCall.getCacheKey(), apiCall.getResponseClass());
                if (item != null) {
                    // we have at least cache
                    // TODO: show offline warning
                    result = new Result(item.getValue());
                    result.sendToListener();
                    return;
                }
            }
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
			if (exception != null) {
				Lg.API_LOADER.e("Loader generated exception: "+exception);
                Throwable cause = Utils.findExceptionCause(exception);
				Lg.API_LOADER.e("Cause is: "+cause);

                if (cause instanceof java.net.UnknownHostException) {
                    getListenerProxy().onInternetException(exception);

                } else if (cause instanceof SocketException) {
                    getListenerProxy().onInternetException(exception);

                } else if (cause instanceof ApiCall.ApiServerSideException) {
                    if (((ApiCall.ApiServerSideException)cause).getCode() == ErrorCode.WRONG_AUTH_KEY) {
	                    // logout
	                    Settings.setAuthKey(null);
                    }
	                getListenerProxy().onServerSideException((ApiCall.ApiServerSideException) cause);

                } else {
                    // ok, this really should not happen, let Crittercism handle this
                    throw new IllegalStateException(exception);
                }
            }
		}
	}

    public static interface Listener<RP> {
		public void onResult(RP result);
		public void onInternetException(Exception e);
	    public void onServerSideException(ApiCall.ApiServerSideException e);
    }

}
