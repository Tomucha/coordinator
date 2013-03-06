package cz.clovekvtisni.coordinator.android.event;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.UserInEvent;

public class UsersFragment extends SherlockFragment {
	private EventActivity activity;
	private ListView listView;
	private List<UserInEvent> users = new ArrayList<UserInEvent>();
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
				activity.showUserOnMap(users.get(position));
			}
		});

		return listView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.users, menu);
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
		this.users = users;
		adapter.notifyDataSetChanged();
	}

	private class UserAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return users.size();
		}

		@Override
		public Object getItem(int position) {
			return users.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
			}
			
			User user = users.get(position).getUser();
			
			TextView title = (TextView) convertView.findViewById(android.R.id.text1);
			title.setText(user.getFullName());
			
			TextView desc = (TextView) convertView.findViewById(android.R.id.text2);
			desc.setText(user.getPhone());

			return convertView;
		}

	}
}
