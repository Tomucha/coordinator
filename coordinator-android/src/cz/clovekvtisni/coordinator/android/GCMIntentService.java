package cz.clovekvtisni.coordinator.android;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.fhucho.android.workers.Workers;
import com.google.android.gcm.GCMBaseIntentService;

import cz.clovekvtisni.coordinator.SecretInfo;
import cz.clovekvtisni.coordinator.android.api.ApiCache;
import cz.clovekvtisni.coordinator.android.api.ApiCall;
import cz.clovekvtisni.coordinator.android.api.ApiCalls;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders;
import cz.clovekvtisni.coordinator.android.event.EventActivity;
import cz.clovekvtisni.coordinator.android.other.Settings;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.android.util.UiTool;
import cz.clovekvtisni.coordinator.android.welcome.MainActivity;
import cz.clovekvtisni.coordinator.api.request.EventFilterRequestParams;
import cz.clovekvtisni.coordinator.api.request.EventPoiListRequestParams;
import cz.clovekvtisni.coordinator.api.response.EventFilterResponseData;
import cz.clovekvtisni.coordinator.api.response.EventPoiFilterResponseData;
import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.domain.NotificationType;
import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.domain.config.Organization;

import java.io.IOException;
import java.util.Date;

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

        // TODO: teoreticky NPE
        Long poiId = Long.parseLong(intent.getStringExtra("poiId"));
        Long eventId = Long.parseLong(intent.getStringExtra("eventId"));
        Double latitude = Double.parseDouble(intent.getStringExtra("latitude"));
        Double longitude = Double.parseDouble(intent.getStringExtra("longitude"));
        String name = intent.getStringExtra("name");
        String organizationId = intent.getStringExtra("organizationId");

        loadEvent(eventId, organizationId, type, name, poiId);

    }

    private void loadEvent(final long eventId, final String organizationId, final String type, final String poiName, final long poiId) {
        try {

            EventFilterRequestParams params = new EventFilterRequestParams();
            params.setOrganizationId(organizationId);

            final ApiCall<EventFilterRequestParams, EventFilterResponseData> apiCall = new ApiCalls.EventRegisteredCall(params);

            final ApiCache cache = ApiCache.getInstance();

            final ApiCache.Item<EventFilterResponseData> item = cache.get(apiCall.getCacheKey(), apiCall.getResponseClass());
            if (item == null || item.getValue() == null) {
                // well, we need to load it

                // FIXME: tohle neni hezky, ale jezto mi Hejl vyhazel asynchronni API volani, tak nemam moc na vyber

                AsyncTask<ApiCall, Void, EventFilterResponseData> task = new AsyncTask<ApiCall, Void, EventFilterResponseData>() {
                    @Override
                    protected EventFilterResponseData doInBackground(ApiCall... apiCalls) {
                        try {
                            Lg.GCM.i("Loading fresh data");
                            return apiCall.call();
                        } catch (Exception e) {
                            Lg.GCM.e("Cannot load fresh data: "+e, e);
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(EventFilterResponseData eventFilterResponseData) {
                        if (eventFilterResponseData == null) {
                            notifyMessage(type, poiName, poiId, null, null);
                            return;
                        }
                        cache.put(apiCall.getCacheKey(), eventFilterResponseData);
                        notifyMessage(type, poiName, poiId,
                                findEvent(eventId, eventFilterResponseData.getOrganizationInEvents()),
                                findOrganizationInEvent(organizationId, eventId, eventFilterResponseData.getOrganizationInEvents())
                        );
                    }
                };

            } else {
                notifyMessage(type, poiName, poiId,
                        findEvent(eventId, item.getValue().getOrganizationInEvents()),
                        findOrganizationInEvent(organizationId, eventId, item.getValue().getOrganizationInEvents())
                );
            }

        } catch (Exception e) {
            Lg.GCM.e("Cannot render notification: "+e, e);
        }

    }



    private OrganizationInEvent findOrganizationInEvent(String organizationId, long eventId, OrganizationInEvent[] organizationInEvents) {
        for (OrganizationInEvent organizationInEvent : organizationInEvents) {
            if (organizationInEvent.getEventId() == eventId && organizationInEvent.getOrganizationId().equals(organizationId)) return organizationInEvent;
        }
        return null;
    }

    private Event findEvent(long eventId, OrganizationInEvent[] events) {
        for (OrganizationInEvent event : events) {
            if (eventId == event.getEventId()) return event.getEvent();
        }
        return null;
    }

    @Override
	protected void onUnregistered(Context context, String registrationId) {
		Lg.GCM.i("Device unregistered.");
        Settings.setGcmRegistrationId(null);
	}

    private void notifyMessage(String type, String poiName, long poiId, Event event, OrganizationInEvent organizationInEvent) {

        if (event == null || organizationInEvent == null) {
            fallbackNotification();
            return;
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(poiName == null ? getString(R.string.app_name) : poiName);

        if (NotificationType.ASSIGN.toString().equals(type)) {
            mBuilder.setContentText(getString(R.string.notification_assigned));

        } else if (NotificationType.UNASSIGN.toString().equals(type)) {
            mBuilder.setContentText(getString(R.string.notification_unassigned));

        } else {
            mBuilder.setContentText(getString(R.string.notification_unknown));
        }

        mBuilder.setVibrate(new long[] {200, 200, 500});
        mBuilder.setLights(Color.RED, 800, 400);

        Intent resultIntent = EventActivity.IntentHelper.create(this, event, organizationInEvent, poiId);
        resultIntent.setAction("DUMMY_ACTION_TO_MAKE_INTENTS_DIFFERENT_"+System.currentTimeMillis());

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(UiTool.DEFAULT_NOTIFICATION, mBuilder.getNotification());
    }

    /**
     *
     * This creates simple dummy notification.
     */
    private void fallbackNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name));

            mBuilder.setContentText(getString(R.string.notification_unknown));

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction("DUMMY_ACTION_TO_MAKE_INTENTS_DIFFERENT_"+System.currentTimeMillis());

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(UiTool.DEFAULT_NOTIFICATION, mBuilder.getNotification());
    }

}
