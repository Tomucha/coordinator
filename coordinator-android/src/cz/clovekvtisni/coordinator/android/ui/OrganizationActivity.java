package cz.clovekvtisni.coordinator.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.domain.config.Organization;

public class OrganizationActivity extends SherlockFragmentActivity {

	private Organization organization;

	private void initWebView() {
		WebView webView = (WebView) findViewById(R.id.webView);
		webView.loadUrl("http://news.ycombinator.com");
	}

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_organization);

		organization = IntentHelper.getOrganization(getIntent());

		getSupportActionBar().setTitle(organization.getName());
		
		initWebView();
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
