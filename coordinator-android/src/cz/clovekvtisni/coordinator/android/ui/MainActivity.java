package cz.clovekvtisni.coordinator.android.ui;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ApiCallAsyncLoader;
import cz.clovekvtisni.coordinator.android.api.ApiCallFactory;
import cz.clovekvtisni.coordinator.android.util.CommonTool;
import cz.clovekvtisni.coordinator.api.response.GlobalConfigResponse;

public class MainActivity extends SherlockFragmentActivity implements
		LoaderCallbacks<GlobalConfigResponse> {

	private static final int LOADER_CONFIG = 1;

	private TextView labelText;
	private ApiCallFactory apiCallFactory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		labelText = (TextView) findViewById(R.id.label);
		apiCallFactory = new ApiCallFactory();

		// immediatelly start loading config data
		getSupportLoaderManager().initLoader(LOADER_CONFIG, null, this);

		// show progress
		labelText.setText("Loading...");
	}

	@Override
	public Loader<GlobalConfigResponse> onCreateLoader(int arg0, Bundle arg1) {
		// create async loader based on ApiCallFactory
		return new ApiCallAsyncLoader<Void, GlobalConfigResponse>(getApplicationContext(),
				apiCallFactory.globalConfiguration(), null);
	}

	@Override
	public void onLoadFinished(Loader<GlobalConfigResponse> loader, GlobalConfigResponse config) {
		// hide progress
		labelText.setText("Done");

		// check loader exception
		Exception e = ((ApiCallAsyncLoader) loader).getException();
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