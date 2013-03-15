package com.fhucho.android.workers;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class Workers {
	
	public static ActivityWorker<?, ?> getActivityWorker(Object id) {
		// TODO
		return null;
	}

	public static <L> Loader<L> load(Loader<L> loaderPrototype, L listener,
			FragmentActivity activity) {
		Utils.checkOnUiThread();
		
		WorkersFragment frag = Workers.getOrCreateFragment(activity);

		Loader<L> equivalentLoader = frag.findEquivalentLoader(loaderPrototype);
		Loader<L> loader = null;
		if (equivalentLoader == null) {
			loader = loaderPrototype;
			frag.addLoader(loader);
			loader.start();
		} else {
			loader = equivalentLoader;
		}

		loader.setListener(listener);

		return loader;
	}

	public static <L, A extends FragmentActivity> ActivityWorker<L, A> start(final ActivityWorker<L, A> task,
			A activity) {
		Utils.checkOnUiThread();
		
		WorkersFragment frag = Workers.getOrCreateFragment(activity);
		task.start(frag, activity);

		return task;
	}

	private static WorkersFragment getOrCreateFragment(FragmentActivity activity) {
		Utils.checkOnUiThread();
		
		FragmentManager fm = activity.getSupportFragmentManager();
		WorkersFragment frag = (WorkersFragment) fm.findFragmentByTag(WorkersFragment.TAG);
		if (frag == null) {
			frag = new WorkersFragment();
			fm.beginTransaction().add(frag, WorkersFragment.TAG).commit();
		}

		return frag;
	}

}
