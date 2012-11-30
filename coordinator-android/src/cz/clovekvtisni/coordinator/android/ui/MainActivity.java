package cz.clovekvtisni.coordinator.android.ui;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.View.OnClickListener;
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
import cz.clovekvtisni.coordinator.api.request.EmptyRequestParams;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
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
	
	private void initTryAgainButton() {
		findViewById(R.id.try_again).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadOrganizations();
			}
		});
	}
	
	private void loadOrganizations() {
		onLoadingStateChanged(LoadingState.LOADING);
		getSupportLoaderManager().restartLoader(LOADER_CONFIG, null, configCallbacks);
	}

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_main);

		initTryAgainButton();
		initListView();

		loadOrganizations();
	}

	private void onLoadingStateChanged(LoadingState state) {
		int loadingVisibility = state == LoadingState.LOADING ? View.VISIBLE : View.GONE;
		findViewById(R.id.loading_overlay).setVisibility(loadingVisibility);
		
		int errorVisibility = state == LoadingState.ERROR ? View.VISIBLE : View.GONE;
		findViewById(R.id.error_overlay).setVisibility(errorVisibility);
	}

	private void onOrganizationsLoaded(Organization[] organizations) {
		adapter.setOrganizations(organizations);
		adapter.notifyDataSetChanged();
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

	private class ConfigCallbacks implements LoaderCallbacks<ConfigResponse> {
		@Override
		public Loader<ConfigResponse> onCreateLoader(int id, Bundle args) {
			return new ApiCallAsyncLoader<EmptyRequestParams, ConfigResponse>(
					getApplicationContext(), ApiCallFactory.configuration(),
					new EmptyRequestParams());
		}

		@Override
		public void onLoadFinished(Loader<ConfigResponse> loader, ConfigResponse config) {
			ApiCallAsyncLoader apiLoader = (ApiCallAsyncLoader) loader;
			Exception e = apiLoader.getException();
			if (e != null) {
				onLoadingStateChanged(LoadingState.ERROR);
			} else {
				onOrganizationsLoaded(config.getOrganizationList());
				onLoadingStateChanged(LoadingState.DONE);
			}
		}

		@Override
		public void onLoaderReset(Loader<ConfigResponse> loader) {
		}
	}

	private enum LoadingState {
		LOADING, ERROR, DONE
	}

}