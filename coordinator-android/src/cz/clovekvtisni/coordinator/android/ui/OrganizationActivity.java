package cz.clovekvtisni.coordinator.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ApiCallAsyncLoader;
import cz.clovekvtisni.coordinator.android.api.ApiCallFactory;
import cz.clovekvtisni.coordinator.android.register.RegisterActivity;
import cz.clovekvtisni.coordinator.android.util.CommonTool;
import cz.clovekvtisni.coordinator.api.request.OrganizationEventsRequestParams;
import cz.clovekvtisni.coordinator.api.response.OrganizationEventsResponseData;
import cz.clovekvtisni.coordinator.domain.config.Organization;

public class OrganizationActivity extends SherlockFragmentActivity {

	private static final int LOADER_EVENTS = 0;

	private final EventsCallbacks eventsCallbacks = new EventsCallbacks();

	private Organization organization;

	private void initPreregisterButton() {
		findViewById(R.id.preregister).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(RegisterActivity.IntentHelper.create(getBaseContext(), organization));
			}
		});
	}
	
	private void initWebView() {
		WebView webView = (WebView) findViewById(R.id.webView);
	}

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_organization);

		organization = IntentHelper.getOrganization(getIntent());

		getSupportActionBar().setTitle(organization.getName());

		initWebView();
		initPreregisterButton();

		getSupportLoaderManager().initLoader(LOADER_EVENTS, null, eventsCallbacks);
	}

	private class EventsCallbacks implements LoaderCallbacks<OrganizationEventsResponseData> {
		@Override
		public Loader<OrganizationEventsResponseData> onCreateLoader(int id, Bundle args) {
			OrganizationEventsRequestParams params = new OrganizationEventsRequestParams();
			params.setOrganizationId(organization.getId());

			return new ApiCallAsyncLoader<OrganizationEventsRequestParams, OrganizationEventsResponseData>(
					getApplicationContext(), ApiCallFactory.organizationEvents(), params);
		}

		@Override
		public void onLoadFinished(Loader<OrganizationEventsResponseData> loader,
				OrganizationEventsResponseData result) {
			ApiCallAsyncLoader apiLoader = (ApiCallAsyncLoader) loader;
			Exception e = apiLoader.getException();
			if (e != null) {
				CommonTool.showToast(OrganizationActivity.this, e.toString());
			} else {
				System.out.println(result.getOrganizationInEvents().size());
			}
		}

		@Override
		public void onLoaderReset(Loader<OrganizationEventsResponseData> loader) {
		}
	}
	
	public static class IntentHelper {
		private static final String EXTRA_ORGANIZATION = "organization";

		public static Intent create(Context c, Organization o) {
			Intent i = new Intent(c, OrganizationActivity.class);
			i.putExtra(EXTRA_ORGANIZATION, o);
			return i;
		}

		public static Organization getOrganization(Intent i) {
			return (Organization) i.getSerializableExtra(EXTRA_ORGANIZATION);
		}
	}

}
