package com.fhucho.android.workers.simple;

import android.support.v4.app.FragmentActivity;

import com.fhucho.android.workers.ActivityWorker;

public abstract class ActivityWorker2<A extends FragmentActivity, S, E extends Exception>
		extends ActivityWorker<ActivityWorkerListener2<S, E>, A> implements
		ActivityWorkerListener2<S, E> {

	@SuppressWarnings("unchecked")
	public ActivityWorker2() {
		super((Class<ActivityWorkerListener2<S, E>>) new ActivityWorkerListener2<S, E>() {
					@Override
					public void onSuccess(S result) {
					}

					@Override
					public void onException(E exception) {
					}
				}.getClass().getInterfaces()[0]);
	}

	@Override
	public void onSuccess(S result) {
	}

	@Override
	public void onException(E exception) {
	}

}
