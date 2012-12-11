package cz.clovekvtisni.coordinator.android.ui;

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

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ConfigCall;
import cz.clovekvtisni.coordinator.android.workers.Workers;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
import cz.clovekvtisni.coordinator.domain.config.Organization;

public class MainActivity extends SherlockFragmentActivity {

	private final OrganizationAdapter adapter = new OrganizationAdapter();

	private Organization[] organizations;
	private Workers workers;

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

	private void initOrganizations(Bundle state) {
		if (state != null && state.containsKey("organizations")) {
			organizations = (Organization[]) state.getSerializable("organizations");
		}
		if (organizations == null) {
			loadOrganizations();
		}
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

		workers.startOrConnect(new ConfigCall(), new ConfigCall.Listener() {
			@Override
			public void onResult(ConfigResponse result) {
				setLoadingState(LoadingState.DONE);
				organizations = result.getOrganizationList();
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onException(Exception e) {
				setLoadingState(LoadingState.ERROR);
			}
		});
	}

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_main);

		workers = new Workers(this);

		initTryAgainButton();
		initOrganizations(state);
		initListView();
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("organizations", organizations);
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