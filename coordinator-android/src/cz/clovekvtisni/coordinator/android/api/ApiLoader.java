package cz.clovekvtisni.coordinator.android.api;

import com.fhucho.android.workers.Loader;

import cz.clovekvtisni.coordinator.android.api.ApiCache.Item;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.android.util.UiTool;
import cz.clovekvtisni.coordinator.api.request.RequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponseData;

import java.net.SocketException;

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
	protected synchronized void doInBackground() {
        ApiCache cache = ApiCache.getInstance();
        Item<RP> item = cache.get(apiCall.getCacheKey(), apiCall.getResponseClass());

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

	public void reload() {
		reload = true;
        // FIXME: no tak startovat furt novy thready, to teda nevim
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
			if (exception != null) {
                Throwable cause = findExceptionCause(exception);

                if (cause instanceof java.net.UnknownHostException) {
                    getListenerProxy().onInternetException(exception);

                } else if (cause instanceof SocketException) {
                    getListenerProxy().onInternetException(exception);

                } else {
                    // ok, this really should not happen, let Crittercism handle this
                    throw new IllegalStateException(exception);
                }
            }
		}
	}

    private Throwable findExceptionCause(Throwable exception) {
        while (exception.getCause() != null && exception.getCause() != exception) {
            exception = exception.getCause();
        }
        return exception;
    }

    public static interface Listener<RP> {
		public void onResult(RP result);
		public void onInternetException(Exception e);
	}

}
