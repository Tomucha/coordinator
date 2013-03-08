package cz.clovekvtisni.coordinator.android;

public class DeployEnvironment {
	public static final String CRITTERCISM_APP_ID;

	static {
		if (BuildConfig.DEBUG) {
			CRITTERCISM_APP_ID = "5139d88f5483087337000033";
		}
	}
}
