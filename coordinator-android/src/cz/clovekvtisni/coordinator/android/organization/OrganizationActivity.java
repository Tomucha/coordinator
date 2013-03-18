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
import com.fhucho.android.workers.Workers;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.EventRegisteredLoader;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.EventRegisteredLoaderListener;
import cz.clovekvtisni.coordinator.android.event.EventActivity;
import cz.clovekvtisni.coordinator.android.register.RegisterActivity;
import cz.clovekvtisni.coordinator.api.request.EventFilterRequestParams;
import cz.clovekvtisni.coordinator.api.response.EventFilterResponseData;
import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.domain.config.Organization;

public class OrganizationActivity extends SherlockFragmentActivity {

	private static final int REQUEST_REGISTER = 0;

	private EventsAdapter adapter;
	private Organization organization;
	private OrganizationInEvent[] orgInEvents;
	private Set<Long> registeredEventIds = new HashSet<Long>();
	private View preregister;

	private Event[] events = new Event[0];

	private void initEvents() {
		adapter = new EventsAdapter();

		ListView listView = (ListView) findViewById(R.id.events);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Context c = OrganizationActivity.this;
				Event event = events[position];
				boolean registered = registeredEventIds.contains(event.getId());
				if (registered) {
					startActivity(EventActivity.IntentHelper.create(c, events[position],
							orgInEvents[position]));
				} else {
					startActivityForResult(
							RegisterActivity.IntentHelper.create(c, organization, event),
							REQUEST_REGISTER);
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
						null));
			}
		});
	}

	private void loadEvents() {
		EventFilterRequestParams params = new EventFilterRequestParams();
		params.setOrganizationId(organization.getId());

		Workers.load(new EventRegisteredLoader(params), new EventRegisteredLoaderListener() {
			@Override
			public void onException(Exception e) {
			}

			@Override
			public void onResult(EventFilterResponseData result) {
				UserInEvent[] userInEvents = result.getUserInEvents();
				if (userInEvents == null) userInEvents = new UserInEvent[0];

				OrganizationInEvent[] orgInEvents = result.getOrganizationInEvents();
				if (orgInEvents == null) orgInEvents = new OrganizationInEvent[0];

				Event[] events = result.getEvents();
				if (events == null) events = new Event[0];

				onEventsLoaded(events, userInEvents, orgInEvents);
			}
		}, this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_REGISTER) {
			EventFilterRequestParams params = new EventFilterRequestParams();
			params.setOrganizationId(organization.getId());

			Workers.load(new EventRegisteredLoader(params), new EventRegisteredLoaderListener() {
				@Override
				public void onException(Exception e) {
				}

				@Override
				public void onResult(EventFilterResponseData result) {
					UserInEvent[] userInEvents = result.getUserInEvents();
					if (userInEvents == null) userInEvents = new UserInEvent[0];

					OrganizationInEvent[] orgInEvents = result.getOrganizationInEvents();
					if (orgInEvents == null) orgInEvents = new OrganizationInEvent[0];

					Event[] events = result.getEvents();
					if (events == null) events = new Event[0];

					onEventsLoaded(events, userInEvents, orgInEvents);
				}
			}, this);
		}
	}

	private void onEventsLoaded(Event[] events, UserInEvent[] userInEvents,
			OrganizationInEvent[] orgInEvents) {
		this.events = events;
		this.orgInEvents = orgInEvents;
		
		registeredEventIds.clear();
		for (UserInEvent userInEvent : userInEvents) {
			registeredEventIds.add(userInEvent.getId());
		}

		adapter.notifyDataSetChanged();
		if (events.length == 0) preregister.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_organization);

		organization = IntentHelper.getOrganization(getIntent());

		getSupportActionBar().setTitle(organization.getName());

		initEvents();
		initPreregisterButton();

		loadEvents();
	}

	private class EventsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return events.length;
		}

		@Override
		public Object getItem(int position) {
			return events[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_2,
						parent, false);
			}

			Event event = events[position];
			boolean registered = registeredEventIds.contains(event.getId());

			TextView title = (TextView) convertView.findViewById(android.R.id.text1);
			title.setText(event.getName() + (registered ? " (zaregistrován)" : ""));

			TextView desc = (TextView) convertView.findViewById(android.R.id.text2);
			desc.setText(event.getDescription());

			return convertView;
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
