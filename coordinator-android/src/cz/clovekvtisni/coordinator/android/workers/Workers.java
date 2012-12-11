package cz.clovekvtisni.coordinator.android.workers;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class Workers {
	private final FragmentActivity activity;

	public Workers(FragmentActivity activity) {
		this.activity = activity;
	}
	
	public <T> void connectIfRunning(Object workerId, T listener) {
		WorkersFragment frag = getFragmentCreateIfNeeded();
		
		@SuppressWarnings("unchecked")
		Worker<T> worker = (Worker<T>) frag.getWorker(workerId);
		if(worker != null) {
			worker.setListener(listener);
		}
	}

	private WorkersFragment getFragmentCreateIfNeeded() {
		FragmentManager fm = activity.getSupportFragmentManager();
		WorkersFragment frag = (WorkersFragment) fm.findFragmentByTag(WorkersFragment.TAG);
		if (frag == null) {
			frag = new WorkersFragment();
			fm.beginTransaction().add(frag, WorkersFragment.TAG).commit();
		}

		return frag;
	}

	public <T> Worker<T> startOrConnect(Worker<T> worker, T listener) {
		WorkersFragment frag = getFragmentCreateIfNeeded();

		@SuppressWarnings("unchecked")
		Worker<T> existingWorker = (Worker<T>) frag.getWorker(worker.getId());
		
		if (existingWorker == null) {
			frag.addWorker(worker.getId(), worker);
			worker.setListener(listener);
			worker.start();
			return worker;
		} else {
			existingWorker.setListener(listener);
			return existingWorker;
		}
	}
	
	public <T> void start(Worker<T> worker, T listener) {
		WorkersFragment frag = getFragmentCreateIfNeeded();
		
		frag.addWorker(worker.getId(), worker);
		worker.setListener(listener);
		worker.start();
	}
	
}
