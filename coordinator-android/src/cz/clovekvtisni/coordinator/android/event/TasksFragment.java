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

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.util.BetterArrayAdapter;
import cz.clovekvtisni.coordinator.android.util.FindView;
import cz.clovekvtisni.coordinator.domain.Poi;

public class TasksFragment extends SherlockFragment {

	private EventActivity activity;
	private PoisAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);

		activity = (EventActivity) getActivity();

		adapter = new PoisAdapter(new ArrayList<Poi>());
		ListView listView = new ListView(getActivity());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				activity.showPoiOnMap(adapter.getItem(position));
			}
		});

		return listView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.tasks, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_filter_tasks:
			activity.showPoiFilterDialog();
			break;
		}
		return true;
	}

	public void setFilteredPois(List<Poi> pois) {
		adapter.clear();
		adapter.addAll(pois);
	}

	private class PoisAdapter extends BetterArrayAdapter<Poi> {

		public PoisAdapter(List<Poi> pois) {
			super(getActivity(), android.R.layout.simple_list_item_2);
		}

		@Override
		protected void setUpView(Poi poi, View view) {
			FindView.textView(view, android.R.id.text1).setText(poi.getName());
			FindView.textView(view, android.R.id.text2).setText(poi.getDescription());
		}

	}
}
