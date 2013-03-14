package cz.clovekvtisni.coordinator.android.welcome;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.fhucho.android.workers.Workers;
import com.google.android.gcm.GCMRegistrar;

import cz.clovekvtisni.coordinator.SecretInfo;
import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.ConfigLoader;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.ConfigLoaderListener;
import cz.clovekvtisni.coordinator.android.organization.OrganizationActivity;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
import cz.clovekvtisni.coordinator.domain.config.Organization;

public class MainActivity extends SherlockFragmentActivity {

	private final OrganizationAdapter adapter = new OrganizationAdapter();

	private Organization[] organizations;

	private void initGCM() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			Lg.GCM.d("Going to register.");
			GCMRegistrar.register(this, SecretInfo.GCM_SENDER_ID);
		} else {
			Lg.GCM.d("Already registered.");
		}
	}

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
		setLoadingState(LoadingState.LOADING);

		Workers.load(new ConfigLoader(), new ConfigLoaderListener() {
			@Override
			public void onResult(ConfigResponse result) {
				setLoadingState(LoadingState.DONE);
				organizations = result.getOrganizationList();
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onException(Exception e) {
				e.printStackTrace();
				setLoadingState(LoadingState.ERROR);
			}
		}, this);
	}

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_main);

		initTryAgainButton();
		loadOrganizations();
		initListView();
		initGCM();
	}

	private void setLoadingState(LoadingState state) {
		int loadingVisibility = state == LoadingState.LOADING ? View.VISIBLE : View.GONE;
		findViewById(R.id.loading_overlay).setVisibility(loadingVisibility);

		int errorVisibility = state == LoadingState.ERROR ? View.VISIBLE : View.GONE;
		findViewById(R.id.error_overlay).setVisibility(errorVisibility);
	}

	private void onOrganizationSelected(Organization organization) {
		startActivity(OrganizationActivity.IntentHelper.create(this, organization));
	}

	private class OrganizationAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			if (organizations == null) return 0;
			else return organizations.length;
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
	}

	private enum LoadingState {
		LOADING, ERROR, DONE
	}

}