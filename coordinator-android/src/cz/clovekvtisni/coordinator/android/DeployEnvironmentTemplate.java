package cz.clovekvtisni.coordinator.android;

public class DeployEnvironmentTemplate {

	public static final String CRITTERCISM_APP_ID;
	public static final String SERVER_URL_PREFIX;

	static {
		if (BuildConfig.DEBUG) {
			CRITTERCISM_APP_ID = null; // fill your realone if you use crittercism
			SERVER_URL_PREFIX = "https://my-test.appspot.com";
		} else {
            CRITTERCISM_APP_ID = null; // fill your realone if you use crittercism
			SERVER_URL_PREFIX = "https://my-test.appspot.com";
		}
	}
}
