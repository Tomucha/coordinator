package cz.clovekvtisni.coordinator.android.organization;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.OrganizationEventsCall;
import cz.clovekvtisni.coordinator.android.event.EventActivity;
import cz.clovekvtisni.coordinator.android.register.RegisterActivity;
import cz.clovekvtisni.coordinator.android.workers.Workers;
import cz.clovekvtisni.coordinator.api.request.OrganizationEventsRequestParams;
import cz.clovekvtisni.coordinator.api.response.OrganizationEventsResponseData;
import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.domain.config.Organization;

public class OrganizationActivity extends SherlockFragmentActivity {

	private Organization organization;
	private Workers workers;

	private void initEvents() {
		findViewById(R.id.event1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(EventActivity.IntentHelper.create(OrganizationActivity.this));
			}
		});
	}
	
	private void initPreregisterButton() {
		findViewById(R.id.preregister).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(RegisterActivity.IntentHelper.create(getBaseContext(), organization));
			}
		});
	}

	private void initWebView() {
		// WebView webView = (WebView) findViewById(R.id.webView);
	}

	private void loadEvents() {
		OrganizationEventsRequestParams params = new OrganizationEventsRequestParams();
		params.setOrganizationId(organization.getId());
		OrganizationEventsCall call = new OrganizationEventsCall(params);

		workers.startOrConnect(call, new OrganizationEventsCall.Listener() {
			@Override
			public void onException(Exception e) {
			}

			@Override
			public void onResult(OrganizationEventsResponseData result) {
				for(OrganizationInEvent event:result.getOrganizationInEvents()) {
					System.out.println(event.getName());
				}
			}
		});
	}

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_organization);

		organization = IntentHelper.getOrganization(getIntent());
		workers = new Workers(this);

		getSupportActionBar().setTitle(organization.getName());

		initWebView();
		initEvents();
		initPreregisterButton();

		loadEvents();
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
