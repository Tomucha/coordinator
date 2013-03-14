package com.fhucho.android.workers.simple;

import android.support.v4.app.FragmentActivity;

import com.fhucho.android.workers.ActivityWorker;

public abstract class ActivityWorker1<A extends FragmentActivity, S> extends
		ActivityWorker<ActivityWorkerListener1<S>, A> implements ActivityWorkerListener1<S> {

	@SuppressWarnings("unchecked")
	public ActivityWorker1() {
		super((Class<ActivityWorkerListener1<S>>) new ActivityWorkerListener1<S>() {
			@Override
			public void onSuccess(S result) {
			}
		}.getClass().getInterfaces()[0]);
	}

	@Override
	public void onSuccess(S result) {
	}
}
