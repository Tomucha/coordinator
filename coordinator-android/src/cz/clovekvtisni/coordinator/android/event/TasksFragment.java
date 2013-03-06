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
import cz.clovekvtisni.coordinator.domain.Poi;

public class TasksFragment extends SherlockFragment {

	private EventActivity activity;
	private ListView listView;
	private List<Poi> pois = new ArrayList<Poi>();
	private PoisAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		
		activity = (EventActivity) getActivity();

		adapter = new PoisAdapter();
		listView = new ListView(getActivity());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				activity.showPoiOnMap(pois.get(position));
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
		this.pois = pois;
		adapter.notifyDataSetChanged();
	}

	private class PoisAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return pois.size();
		}

		@Override
		public Object getItem(int position) {
			return pois.get(position);
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
			
			Poi poi = pois.get(position);

			TextView title = (TextView) convertView.findViewById(android.R.id.text1);
			title.setText(poi.getName());
			
			TextView desc = (TextView) convertView.findViewById(android.R.id.text2);
			desc.setText(poi.getDescription());

			return convertView;
		}

	}
}
