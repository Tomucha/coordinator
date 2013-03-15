package com.fhucho.android.workers.simple;

public interface ActivityWorkerListener2<S, E extends Exception> {
	public void onSuccess(S result);

	public void onException(E exception);
}
