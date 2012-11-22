package cz.clovekvtisni.coordinator.android.ui;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ApiCallAsyncLoader;
import cz.clovekvtisni.coordinator.android.api.ApiCallFactory;
import cz.clovekvtisni.coordinator.android.util.CommonTool;
import cz.clovekvtisni.coordinator.api.response.GlobalConfigResponse;
import cz.clovekvtisni.coordinator.domain.config.Organization;

public class MainActivity extends SherlockFragmentActivity {

	private static final int LOADER_CONFIG = 0;

	private final ConfigCallbacks configCallbacks = new ConfigCallbacks();
	private final OrganizationAdapter adapter = new OrganizationAdapter();

	private void initListView() {
		ListView listView = (ListView) findViewById(R.id.list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onOrganizationSelected(adapter.getItem(position));
			}
		});
	}

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_main);

		initListView();

		getSupportLoaderManager().initLoader(LOADER_CONFIG, null, configCallbacks);
	}

	private void updateLoadingUi(LoadingState state) {
		int loadingVisibility = state == LoadingState.LOADING ? View.VISIBLE : View.GONE;
		findViewById(R.id.loading_overlay).setVisibility(loadingVisibility);
	}

	private void onOrganizationsLoaded(Organization[] organizations) {
		adapter.setOrganizations(organizations);
		adapter.notifyDataSetChanged();
		updateLoadingUi(LoadingState.DONE);
	}

	private void onOrganizationSelected(Organization organization) {
		startActivity(OrganizationActivity.IntentHelper.create(this, organization));
	}

	private class OrganizationAdapter extends BaseAdapter {
		private Organization[] organizations = {};

		@Override
		public int getCount() {
			return organizations.length;
		}

		@Override
		public Organization getItem(int position) {
			return organizations[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = (TextView) convertView;
			if (textView == null) {
				textView = (TextView) getLayoutInflater().inflate(
						android.R.layout.simple_list_item_1, null);
			}

			textView.setText(organizations[position].getName());

			return textView;
		}

		private void setOrganizations(Organization[] organizations) {
			this.organizations = organizations;
		}
	}

	private class ConfigCallbacks implements LoaderCallbacks<GlobalConfigResponse> {
		@Override
		public Loader<GlobalConfigResponse> onCreateLoader(int id, Bundle args) {
			return new ApiCallAsyncLoader<Void, GlobalConfigResponse>(getApplicationContext(),
					ApiCallFactory.globalConfiguration(), null);
		}

		@Override
		public void onLoadFinished(Loader<GlobalConfigResponse> loader, GlobalConfigResponse config) {
			ApiCallAsyncLoader apiLoader = (ApiCallAsyncLoader) loader;
			Exception e = apiLoader.getException();
			if (e != null) {
				CommonTool.showToast(MainActivity.this, e.toString());
			} else {
				onOrganizationsLoaded(config.getOrganizationList());
			}
		}

		@Override
		public void onLoaderReset(Loader<GlobalConfigResponse> loader) {
		}
	}

	private enum LoadingState {
		LOADING, ERROR, DONE
	}

}