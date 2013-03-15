package com.fhucho.android.workers.simple;

public interface ActivityWorkerListener3<S, E extends Exception, P> {
	public void onSuccess(S result);

	public void onException(E exception);

	public void onProgress(P progress);
}
