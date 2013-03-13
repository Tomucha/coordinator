package cz.clovekvtisni.coordinator.android;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;

import cz.clovekvtisni.coordinator.SecretInfo;
import cz.clovekvtisni.coordinator.android.other.Settings;
import cz.clovekvtisni.coordinator.android.util.Lg;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super(SecretInfo.GCM_SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Lg.GCM.d("Device registered, regId: " + registrationId + ".");
		Settings.setGcmRegistrationId(registrationId);
	}

	@Override
	protected void onError(Context context, String errorId) {
		Lg.GCM.d("Received error: " + errorId);

	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Lg.GCM.d("Received message. Extras: " + intent.getExtras());
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Lg.GCM.d("Device unregistered.");
	}

}
