package com.fhucho.android.workers;

import android.os.Looper;

public class Utils {
	public static void checkOnUiThread() {
		if (Looper.myLooper() != Looper.getMainLooper()) throw new NotOnUiThreadException();
	}

	@SuppressWarnings("serial")
	public static class NotOnUiThreadException extends RuntimeException {
	}
}
