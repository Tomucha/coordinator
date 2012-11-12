package cz.clovekvtisni.coordinator.android.ui;

import android.os.Bundle;

import com.actionbarsherlock.view.Window;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

import cz.clovekvtisni.coordinator.android.R;

/**
 * Activity to view the carousel and view pager indicator with fragments.
 */
public class MainActivity extends RoboSherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

}