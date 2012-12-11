package cz.clovekvtisni.coordinator.android.workers;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;

public class WorkersFragment extends Fragment {
	public static final String TAG = "WorkersFragment";

	private Map<Object, Worker<?>> workers = new HashMap<Object, Worker<?>>();

	public WorkersFragment() {
		setRetainInstance(true);
	}

	public void addWorker(Object id, Worker<?> worker) {
		if (workers.containsKey(id)) {
			throw new RuntimeException("Worker with id " + id + " already exists.");
		}
		workers.put(id, worker);
	}

	public Worker<?> getWorker(Object id) {
		return workers.get(id);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		for(Worker<?> worker: workers.values()) {
			worker.removeListener();
		}
	}
	
}
