package cz.clovekvtisni.coordinator.android.event;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.util.BetterArrayAdapter;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.UserInEvent;

public class UsersFragment extends SherlockFragment {
	private EventActivity activity;
	private ListView listView;
	private UserAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);

		activity = (EventActivity) getActivity();

		adapter = new UserAdapter();
		listView = new ListView(getActivity());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onUserClick(adapter.getItem(position));
			}
		});

		return listView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.users, menu);
	}
	
	private void onUserClick(UserInEvent userInEvent) {
		if (userInEvent.getLastLocationLatitude() != null) {
			activity.showUserOnMap(userInEvent);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_filter_users:
			activity.showPoiFilterDialog();
			break;
		}
		return true;
	}

	public void setFilteredUsers(List<UserInEvent> users) {
		adapter.clear();
		adapter.addAll(users);
	}

	private class UserAdapter extends BetterArrayAdapter<UserInEvent> {

		public UserAdapter() {
			super(getActivity(), android.R.layout.simple_list_item_2, new ArrayList<UserInEvent>());
		}

		@Override
		protected void setUpItem(int position, View item) {
			User user = getItem(position).getUser();

			TextView title = (TextView) item.findViewById(android.R.id.text1);
			title.setText(user.getFullName());

			TextView desc = (TextView) item.findViewById(android.R.id.text2);
			desc.setText(user.getPhone());
		}

	}
}
