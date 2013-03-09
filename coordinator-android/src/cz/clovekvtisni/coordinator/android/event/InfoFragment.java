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

import cz.clovekvtisni.coordinator.android.util.BetterArrayAdapter;
import cz.clovekvtisni.coordinator.domain.Poi;

public class InfoFragment extends SherlockFragment {

	private static final String ARGS_KEY_EVENT_INFO = "eventInfo";

	private EventActivity activity;
	private ImportantPoisAdapter adapter;

	public static InfoFragment newInstance(String eventInfo) {
		InfoFragment f = new InfoFragment();

		Bundle args = new Bundle();
		args.putString(ARGS_KEY_EVENT_INFO, eventInfo);
		f.setArguments(args);

		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = (EventActivity) getActivity();
		adapter = new ImportantPoisAdapter();
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

	public void setImportantPois(List<Poi> importantPois) {
		adapter.clear();
		adapter.addAll(importantPois);
	}

	private class ImportantPoisAdapter extends BetterArrayAdapter<Poi> {

		public ImportantPoisAdapter() {
			super(getActivity(), android.R.layout.simple_list_item_2, new ArrayList<Poi>());
		}

		@Override
		protected void setUpItem(int position, View item) {
			Poi poi = getItem(position);

			TextView title = (TextView) item.findViewById(android.R.id.text1);
			title.setText(poi.getName());

			TextView desc = (TextView) item.findViewById(android.R.id.text2);
			desc.setText(poi.getDescription());
		}

	}
}
