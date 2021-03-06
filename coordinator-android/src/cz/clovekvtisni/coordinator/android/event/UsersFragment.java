package cz.clovekvtisni.coordinator.android.event;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.util.BetterArrayAdapter;
import cz.clovekvtisni.coordinator.android.util.FindView;
import cz.clovekvtisni.coordinator.android.util.UiTool;
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
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                return onUserLongClick(adapter.getItem(position));
            }
        });

		return listView;
	}

    private boolean onUserLongClick(UserInEvent item) {
        String phone = item.getUser().getPhone();
        if (phone != null) {
            Intent callIntent = new Intent(Intent.ACTION_VIEW);
            callIntent.setData(Uri.parse("tel:" + phone));
            startActivity(callIntent);
            return true;
        }
        return false;
    }

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.users, menu);
	}
	
	private void onUserClick(UserInEvent userInEvent) {
		if (userInEvent.getLastLocationLatitude() != null) {
			activity.showUserOnMap(userInEvent);
		} else {
            UiTool.toast(R.string.message_user_position_unknown, getActivity().getApplicationContext());
        }
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_filter_users:
			activity.showPeopleFilterDialog();
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
			super(getActivity(), R.layout.item_with_icon);
		}

		@Override
		protected void setUpView(UserInEvent userInEvent, View view) {
			User user = userInEvent.getUser();			
			FindView.textView(view, R.id.title).setText(user.getFullName());
			FindView.textView(view, R.id.short_description).setText(user.getPhone());
		}

	}

    /**
     * This is a terrible hack of:
     * http://stackoverflow.com/questions/14516804/nullpointerexception-android-support-v4-app-fragmentmanagerimpl-savefragmentbasi
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("DO NOT CRASH", "OK");
        setUserVisibleHint(true);
        super.onSaveInstanceState(outState);
    }

}
