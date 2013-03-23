package com.fhucho.android.workers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.os.Handler;

public abstract class Loader<L> {

	private final Class<? extends L> typeOfL;
	private final Handler handler = new Handler();
	private L listener;
	private L listenerProxy;

	public Loader(Class<? extends L> typeOfL) {
		Utils.checkOnUiThread();
		
		this.typeOfL = typeOfL;
		listenerProxy = typeOfL.cast(Proxy.newProxyInstance(typeOfL.getClassLoader(),
				new Class<?>[] { typeOfL }, invocationHandler));
	}

	protected abstract void doInBackground();

	protected abstract boolean isEquivalentTo(Loader<?> other);

	protected abstract void onListenerAdded();

	protected L getListenerProxy() {
		return listenerProxy;
	}
	
	public Class<? extends L> getTypeOfL() {
		return typeOfL;
	}

	void onDetach() {
		listener = null;
	}

	void setListener(L listener) {
		this.listener = listener;
		onListenerAdded();
	}

	void start() {
		new Thread() {
			public void run() {
                doInBackground();
			};
		}.start();
	}

	private InvocationHandler invocationHandler = new InvocationHandler() {

		@Override
		public Object invoke(Object proxy, final Method method, final Object[] args)
				throws Throwable {
			if(listener == null) return null;
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (listener != null) {
						try {
							method.invoke(listener, args);
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
