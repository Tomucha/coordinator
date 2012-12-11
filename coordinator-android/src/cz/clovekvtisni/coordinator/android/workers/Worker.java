package cz.clovekvtisni.coordinator.android.workers;

import java.util.LinkedList;
import java.util.Queue;

import android.os.Handler;

public abstract class Worker<T> {
	private final Handler handler = new Handler();
	private final Queue<Runnable> messages = new LinkedList<Runnable>();
	
	private T listener;

	protected abstract void doInBackground();
	
	public Object getId() {
		return getClass();
	}
	
	protected T getListener() {
		return listener;
	}

	public final void removeListener() {
		listener = null;
	}

	protected final void send(final Runnable runnable) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (listener == null) {
					messages.add(runnable);
				} else {
					runnable.run();
				}
			}
		});
	}

	public final void setListener(T listener) {
		if (this.listener != null) {
			throw new RuntimeException("This Worker already has a listener.");
		}
		this.listener = listener;

		while (!messages.isEmpty()) {
			messages.remove().run();
		}
	}

	public void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				doInBackground();
			}
		}).start();
	}
}
