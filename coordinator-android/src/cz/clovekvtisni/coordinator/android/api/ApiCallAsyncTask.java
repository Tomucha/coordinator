package cz.clovekvtisni.coordinator.android.api;

import android.content.Context;
import cz.clovekvtisni.coordinator.android.util.CommonTool;
import cz.clovekvtisni.coordinator.android.util.SafeAsyncTask;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.ApiResponse.Status;
import cz.clovekvtisni.coordinator.api.response.ApiResponseData;


/**
 * 
 * Asynchronni obalka nad {@link ApiCall}.
 * 
 * @author tomucha
 *
 * @param <REQUEST>
 * @param <RESPONSE>
 */
public abstract class ApiCallAsyncTask<REQUEST, RESPONSE extends ApiResponseData> extends SafeAsyncTask<ApiResponse<RESPONSE>> {

	private ApiCall<REQUEST, RESPONSE> apiCall;
	private REQUEST request;

	protected ApiCallAsyncTask(Context context, ApiCall<REQUEST, RESPONSE> call, REQUEST request) {
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
		if (t.getStatus() != Status.OK) {
			throw new ApiResponseException(t);
		} else {
			onResponse(t.getData());
		}
	}

	protected abstract void onResponse(RESPONSE data);

}
