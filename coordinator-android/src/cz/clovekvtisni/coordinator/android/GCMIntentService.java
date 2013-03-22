package cz.clovekvtisni.coordinator.android;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.google.android.gcm.GCMBaseIntentService;

import cz.clovekvtisni.coordinator.SecretInfo;
import cz.clovekvtisni.coordinator.android.other.Settings;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.android.welcome.MainActivity;

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
        String type = intent.getStringExtra("type");
        String poiId = intent.getStringExtra("poiId");
        String eventId = intent.getStringExtra("eventId");
        notifyMessage(type, Long.parseLong(eventId), Long.parseLong(poiId));
	}

    @Override
	protected void onUnregistered(Context context, String registrationId) {
		Lg.GCM.d("Device unregistered.");
	}

    private void notifyMessage(String type, long eventId, long poiId) {
        // FIXME: vic informaci v notifikaci
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Nový úkol")
                        .setContentText("V koordinátoru Vám byl přiřazen nový úkol");

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.getNotification());
    }

}
