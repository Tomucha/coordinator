package com.fhucho.android.workers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.Queue;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;

public abstract class ActivityWorker<L, A extends FragmentActivity> {

	private final Handler handler = new Handler();
	private final L listenerProxy;
	private final Queue<Runnable> messages = new LinkedList<Runnable>();

	private boolean destroyed = false;
	private A activity;
	private Thread thread;
	private WorkersFragment fragment;

	public ActivityWorker(Class<? extends L> typeOfL) {
		Utils.checkOnUiThread();
		listenerProxy = typeOfL.cast(Proxy.newProxyInstance(typeOfL.getClassLoader(),
				new Class<?>[] { typeOfL }, invocationHandler));
		// TODO: check that getClass() implements L
		// TODO: check that getClass() is not a non static inner class of the Activity
	}

	protected abstract void doInBackground();
	
	void destroy() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (isDestroyed()) return;
				setDestroyed(true);
				
				fragment.removeActivityWorker(ActivityWorker.this);
				activity = null;
				thread.interrupt();
			}
		});
	}

	void onAttach(FragmentActivity activity) {
		@SuppressWarnings("unchecked")
		A castedActivity = (A) activity;
		this.activity = castedActivity;

		while (!messages.isEmpty()) {
			messages.remove().run();
		}
	}

	void onDetach() {
		activity = null;
	}

	void start(WorkersFragment fragment, A activity) {
		this.fragment = fragment;
		onAttach(activity);
		fragment.addTask(this);

		thread = new Thread() {
			public void run() {
				doInBackground();
				ActivityWorker.this.destroy();
			};
		};
		thread.start();
	}

	protected A getActivity() {
		return activity;
	}
	
	protected Object getId() {
		return getClass();
	}

	protected L getListenerProxy() {
		return listenerProxy;
	}
	
	protected synchronized boolean isDestroyed() {
		return destroyed;
	}
	
	private synchronized void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}

	private InvocationHandler invocationHandler = new InvocationHandler() {

		@Override
		public Object invoke(Object proxy, final Method method, final Object[] args)
				throws Throwable {
			handler.post(new Runnable() {

				@Override
				public void run() {
					if(isDestroyed()) return;
					
					if (activity == null) {
						messages.add(this);
					} else {
						try {
							method.invoke(ActivityWorker.this, args);
						} catch (IllegalArgumentException e) {
							throw new RuntimeException(e);
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						} catch (InvocationTargetException e) {
							throw new RuntimeException(e);
						}
					}
				}
			});

			return null;
		}
	};

}
