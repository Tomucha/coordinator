package cz.clovekvtisni.coordinator.android.api;

import roboguice.util.RoboAsyncTask;
import android.content.Context;
import cz.clovekvtisni.coordinator.android.util.CommonTool;


/**
 * 
 * Asynchronni obalka nad {@link ApiCall}.
 * 
 * @author tomucha
 *
 * @param <REQUEST>
 * @param <RESPONSE>
 */
public abstract class ApiCallAsyncTask<REQUEST, RESPONSE> extends RoboAsyncTask<ApiResponse<RESPONSE>> {

	private ApiCall<REQUEST, RESPONSE> apiCall;
	private REQUEST request;

	protected ApiCallAsyncTask(Context context, ApiCall<REQUEST, RESPONSE> call, REQUEST request) {
		super(context);
		this.apiCall = call;
		this.request = request;
	}

	@Override
	public ApiResponse<RESPONSE> call() throws Exception {
		CommonTool.logI(getClass().getSimpleName(), "Preparing ApiCall: "+apiCall);
		String authKey = null;
		// FIXME: session id
		CommonTool.logI(getClass().getSimpleName(), "Executing ApiCall: "+apiCall);
		return apiCall.doRequest(request, authKey);
	}
	
	@Override
	protected void onFinally() throws RuntimeException {
		// toto at si kazdy prepise sam, pokud chce
		super.onFinally();
	}

	@Override
	protected void onPreExecute() throws Exception {
		// toto at si kazdy prepise sam, pokud chce
		super.onPreExecute();
	}

	/**
	 * Metoda proveri navratovou hodnotu ApiCall, pokud doslo k chybe, tak ji
	 * zpracuje, pokud je odpoved OK, tak ji posle dal.
	 */
	@Override
	protected final void onSuccess(ApiResponse<RESPONSE> t) throws Exception {
		CommonTool.logI(getClass().getSimpleName(), "Processing response: "+t);
		if (t.getMessage() != null) {
			CommonTool.showToast(getContext(), t.getMessage());
		}
		if (t.getStatus().equals(ApiResponseStatus.OK)) {
			onResponse(t.getData());
		} else {
			throw new ApiResponseException(t);
		}
	}

	protected abstract void onResponse(RESPONSE data);

}
