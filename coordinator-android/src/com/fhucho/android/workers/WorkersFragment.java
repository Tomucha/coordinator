package com.fhucho.android.workers;

import java.util.HashSet;
import java.util.Set;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class WorkersFragment extends Fragment {
	public static final String TAG = "WorkersFragment";

	//private Set<Loader<?>> loaders = new HashSet<Loader<?>>();
	private Set<ActivityWorker<?, ?>> tasks = new HashSet<ActivityWorker<?, ?>>();

	public void addLoader(Loader<?> loader) {
		// loaders.add(loader);
	}

	public void addTask(ActivityWorker<?, ?> task) {
		tasks.add(task);
	}

	public <T> Loader<T> findEquivalentLoader(Loader<T> loader) {
		/*
		for (Loader<?> l : loaders) {
			if (loader.getTypeOfL().equals(l.getTypeOfL())) {
				@SuppressWarnings("unchecked")
				Loader<T> possibleMatch = (Loader<T>) l;
				if (possibleMatch.isEquivalentTo(loader)) {
					return possibleMatch;
				}
			}
		}
        */
		return null;
	}
	
	public void removeActivityWorker(ActivityWorker<?, ?> worker) {
		tasks.remove(worker);
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);
		for (ActivityWorker<?, ?> task : tasks) {
			task.onAttach(getActivity());
		}
	}

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		setRetainInstance(true);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		/*
		for (Loader<?> loader : loaders) {
			loader.onDetach();
		}
		*/
		for (ActivityWorker<?, ?> task : tasks) {
			task.onDetach();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		for (ActivityWorker<?, ?> task : tasks) {
			task.destroy();
		}
	}
}
