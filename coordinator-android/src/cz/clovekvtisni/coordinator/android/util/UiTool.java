package cz.clovekvtisni.coordinator.android.util;

import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: tomucha
 * Date: 21.03.13
 * Time: 21:06
 * To change this template use File | Settings | File Templates.
 */
public class UiTool {

    public static final int DEFAULT_NOTIFICATION = 0;

    public static void dropNotification(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(DEFAULT_NOTIFICATION);

    }

    public static void toast(int message, Context context) {
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

}
