package cz.clovekvtisni.coordinator.android.organization;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
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
import com.fhucho.android.workers.Loader;
import com.fhucho.android.workers.Workers;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ApiLoader;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.EventRegisteredLoader;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.EventRegisteredLoaderListener;
import cz.clovekvtisni.coordinator.android.event.EventActivity;
import cz.clovekvtisni.coordinator.android.register.RegisterActivity;
import cz.clovekvtisni.coordinator.android.util.UiTool;
import cz.clovekvtisni.coordinator.api.request.EventFilterRequestParams;
import cz.clovekvtisni.coordinator.api.response.EventFilterResponseData;
import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.domain.config.Organization;

public class OrganizationActivity extends SherlockFragmentActivity {

	private static final int REQUEST_REGISTER = 0;

	private EventsAdapter adapter;
	private Organization organization;
	private OrganizationInEvent[] orgInEvents = new OrganizationInEvent[0];
	private Set<Long> registeredEventIds = new HashSet<Long>();
	private View preregister;

    private User user;

    private void initEvents() {
		adapter = new EventsAdapter();

		ListView listView = (ListView) findViewById(R.id.events);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Context c = OrganizationActivity.this;
                OrganizationInEvent event = orgInEvents[position];
				boolean registered = registeredEventIds.contains(event.getEventId());
				if (registered) {
					startActivity(EventActivity.IntentHelper.create(c, event.getEvent(), event));
				} else {
					startActivityForResult(RegisterActivity.IntentHelper.create(c, organization, event.getEvent(), user), REQUEST_REGISTER);
				}
			}
		});
	}

	private void initPreregisterButton() {
		preregister = findViewById(R.id.preregister);
		preregister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(RegisterActivity.IntentHelper.create(getBaseContext(), organization,
						null, user));
			}
		});
	}

	private void loadEvents() {
		EventFilterRequestParams params = new EventFilterRequestParams();
		params.setOrganizationId(organization.getId());

		Workers.load(new EventRegisteredLoader(params), new EventRegisteredLoaderListener() {
			@Override
			public void onInternetException(Exception e) {
                UiTool.toast(R.string.error_no_internet, getApplicationContext());
            }

			@Override
			public void onResult(EventFilterResponseData result) {
				UserInEvent[] userInEvents = result.getUserInEvents();
				if (userInEvents == null) userInEvents = new UserInEvent[0];

				OrganizationInEvent[] orgInEvents = result.getOrganizationInEvents();
				if (orgInEvents == null) orgInEvents = new OrganizationInEvent[0];

				onEventsLoaded(userInEvents, orgInEvents);
			}
		}, this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_REGISTER) {
			EventFilterRequestParams params = new EventFilterRequestParams();
			params.setOrganizationId(organization.getId());

            EventRegisteredLoader loader = (EventRegisteredLoader) Workers.load(new EventRegisteredLoader(params), new EventRegisteredLoaderListener() {
                @Override
                public void onInternetException(Exception e) {
                    UiTool.toast(R.string.error_no_internet, getApplicationContext());
                }

                @Override
                public void onResult(EventFilterResponseData result) {
                    UserInEvent[] userInEvents = result.getUserInEvents();
                    if (userInEvents == null) userInEvents = new UserInEvent[0];

                    OrganizationInEvent[] orgInEvents = result.getOrganizationInEvents();
                    if (orgInEvents == null) orgInEvents = new OrganizationInEvent[0];

                    onEventsLoaded(userInEvents, orgInEvents);
                }
            }, this);
            loader.reload();
		}
	}

	private void onEventsLoaded(UserInEvent[] userInEvents, OrganizationInEvent[] orgInEvents) {
		this.orgInEvents = orgInEvents;
		
		registeredEventIds.clear();
		for (UserInEvent userInEvent : userInEvents) {
			registeredEventIds.add(userInEvent.getId());
		}

		adapter.notifyDataSetChanged();
		if (orgInEvents.length == 0) preregister.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_organization);

		organization = IntentHelper.getOrganization(getIntent());
        user = IntentHelper.getUser(getIntent());

		getSupportActionBar().setTitle(organization.getName());

		initEvents();
		initPreregisterButton();

		loadEvents();

        String description = organization.getDescription();
        if (description != null) {
            description = description.trim();
        }
        ((TextView)findViewById(R.id.organization_description)).setText(description);
	}

	private class EventsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return orgInEvents.length;
		}

		@Override
		public Object getItem(int position) {
			return orgInEvents[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, parent, false);
			}

			OrganizationInEvent event = orgInEvents[position];
			boolean registered = registeredEventIds.contains(event.getEventId());

			TextView title = (TextView) convertView.findViewById(android.R.id.text1);
			title.setText(event.getName() + (registered ? " (zaregistrován)" : ""));

			TextView desc = (TextView) convertView.findViewById(android.R.id.text2);
			desc.setText(event.getDescription());

			return convertView;
		}

	}

	public static class IntentHelper {
		private static final String EXTRA_ORGANIZATION = "organization";
        private static final String EXTRA_USER = "user";

		public static Intent create(Context c, Organization o, User myself) {
			Intent i = new Intent(c, OrganizationActivity.class);
			i.putExtra(EXTRA_ORGANIZATION, o);
            i.putExtra(EXTRA_USER, myself);
			return i;
		}

		public static Organization getOrganization(Intent i) {
			return (Organization) i.getSerializableExtra(EXTRA_ORGANIZATION);
		}

        public static User getUser(Intent i) {
            return (User) i.getSerializableExtra(EXTRA_USER);
        }

	}

}
