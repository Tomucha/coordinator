package com.fhucho.android.workers.simple;

import com.fhucho.android.workers.ActivityWorker;

import android.support.v4.app.FragmentActivity;

public abstract class ActivityWorker3<A extends FragmentActivity, S, E extends Exception, P>
		extends ActivityWorker<ActivityWorkerListener3<S, E, P>, A> implements
		ActivityWorkerListener3<S, E, P> {

	@SuppressWarnings("unchecked")
	public ActivityWorker3() {
		super((Class<ActivityWorkerListener3<S, E, P>>) new ActivityWorkerListener3<S, E, P>() {
					@Override
					public void onSuccess(S result) {
					}

					@Override
					public void onException(E exception) {
					}

					@Override
					public void onProgress(P progress) {
					}
				}.getClass().getInterfaces()[0]);
	}

	public void onSuccess(S result) {
	}

	public void onException(E exception) {
	}

	public void onProgress(P progress) {
	}

}
