package cz.clovekvtisni.coordinator.android.welcome;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.fhucho.android.workers.Workers;
import com.google.android.gcm.GCMRegistrar;

import cz.clovekvtisni.coordinator.SecretInfo;
import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.ConfigLoader;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.ConfigLoaderListener;
import cz.clovekvtisni.coordinator.android.api.BitmapLoader;
import cz.clovekvtisni.coordinator.android.organization.OrganizationActivity;
import cz.clovekvtisni.coordinator.android.util.BetterArrayAdapter;
import cz.clovekvtisni.coordinator.android.util.FindView;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.android.util.UiTool;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
import cz.clovekvtisni.coordinator.domain.config.Organization;

public class MainActivity extends SherlockFragmentActivity {

	private final Map<Organization, Bitmap> organizationIcons = new HashMap<Organization, Bitmap>();
	private OrganizationAdapter adapter;

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

	private void loadOrganizations() {
		Workers.load(new ConfigLoader(), new ConfigLoaderListener() {
			@Override
			public void onResult(ConfigResponse result) {
				onOrganizationsLoaded(result.getOrganizationList());
			}
			@Override
			public void onInternetException(Exception e) {
                UiTool.toast(R.string.error_no_internet, getApplicationContext());
			}
		}, this);
	}

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_main);

		adapter = new OrganizationAdapter();
		loadOrganizations();
		initListView();
		initGCM();
	}

	private void onOrganizationsLoaded(Organization[] organizations) {
		adapter.clear();
		adapter.addAll(organizations);
		adapter.notifyDataSetChanged();

		for (final Organization organization : organizations) {
			Workers.load(new BitmapLoader(organization.getIcon()), new BitmapLoader.Listener() {
				@Override
				public void onSuccess(Bitmap bitmap) {
					organizationIcons.put(organization, bitmap);
					adapter.notifyDataSetChanged();
				}

				@Override
				public void onException(Exception e) {
                    throw new IllegalStateException(e);
				}
			}, this);
		}
	}

	private void onOrganizationSelected(Organization organization) {
		startActivity(OrganizationActivity.IntentHelper.create(this, organization));
	}

	private class OrganizationAdapter extends BetterArrayAdapter<Organization> {
		public OrganizationAdapter() {
			super(MainActivity.this, R.layout.item_organization);
		}

		@Override
		protected void setUpView(Organization organization, View view) {
			FindView.textView(view, R.id.title).setText(organization.getName());
			FindView.imageView(view, R.id.icon).setImageBitmap(organizationIcons.get(organization));
		}
	}

}