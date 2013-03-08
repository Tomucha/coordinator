package cz.clovekvtisni.coordinator.android;

import android.app.Application;
import android.content.Context;

import com.crittercism.app.Crittercism;

public class CoordinatorApplication extends Application {
	private static Context applicationContext;

	public void onCreate() {
		super.onCreate();
		applicationContext = getApplicationContext();
		Crittercism.init(getApplicationContext(), DeployEnvironment.CRITTERCISM_APP_ID);
	}

	public static Context getAppContext() {
		return applicationContext;
	}

}
