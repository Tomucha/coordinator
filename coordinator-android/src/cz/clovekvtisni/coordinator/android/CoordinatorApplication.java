package cz.clovekvtisni.coordinator.android;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.FROYO;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;

import com.github.kevinsawicki.http.HttpRequest;

import cz.clovekvtisni.coordinator.android.util.CommonTool;

/**
 * Coordinator Mobile application, taken from Android Bootstrap.
 */
public class CoordinatorApplication extends Application {
	
	static {
		CommonTool.setTag("Coordinator");
	}

    /**
     * Create main application
     */
    public CoordinatorApplication() {
        // Disable http.keepAlive on Froyo and below
        if (SDK_INT <= FROYO)
            HttpRequest.keepAlive(false);
    }

    /**
     * Create main application
     *
     * @param context
     */
    public CoordinatorApplication(final Context context) {
        this();
        attachBaseContext(context);
    }

    /**
     * Create main application
     *
     * @param instrumentation
     */
    public CoordinatorApplication(final Instrumentation instrumentation) {
        this();
        attachBaseContext(instrumentation.getTargetContext());
    }
}
