package cz.clovekvtisni.coordinator.android.ui;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.view.Window;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;

import cz.clovekvtisni.coordinator.android.Constants;
import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ApiCallAsyncLoader;
import cz.clovekvtisni.coordinator.android.api.ApiCallFactory;
import cz.clovekvtisni.coordinator.android.util.CommonTool;
import cz.clovekvtisni.coordinator.api.response.GlobalConfigResponse;

/**
 * Application starts here.
 */
public class MainActivity extends RoboSherlockFragmentActivity implements LoaderCallbacks<GlobalConfigResponse> {
	
	@InjectView(R.id.label_text)
	private TextView labelText;
	
	@InjectView(R.id.pb_loading)
	private ProgressBar loading;
	
	// I can inject my own objects, like this factory
	@Inject
	private ApiCallFactory apiCallFactory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// immediatelly start loading config data
		getSupportLoaderManager().initLoader(Constants.LOADER_CONFIG, null, this);
		
		// show progress
		loading.setVisibility(View.VISIBLE);
		labelText.setVisibility(View.GONE);
	}

	@Override
	public Loader<GlobalConfigResponse> onCreateLoader(int arg0, Bundle arg1) {
		// create async loader based on ApiCallFactory
		return new ApiCallAsyncLoader<Void, GlobalConfigResponse>(
				getApplicationContext(),
				apiCallFactory.globalConfiguration(),
				null);
	}

	@Override
	public void onLoadFinished(Loader<GlobalConfigResponse> loader, GlobalConfigResponse config) {
		// hide progress
		loading.setVisibility(View.GONE);
		labelText.setVisibility(View.VISIBLE);
		
		// check loader exception
		Exception e = ((ApiCallAsyncLoader)loader).getException();
		if (e != null) {
			CommonTool.showToast(this, e.toString());
		} else {
			// data
			labelText.setText(config.toString());
		}
	}

	@Override
	public void onLoaderReset(Loader<GlobalConfigResponse> config) {
		labelText.setText(config.toString());
	}

}