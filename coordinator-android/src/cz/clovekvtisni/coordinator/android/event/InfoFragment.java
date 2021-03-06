package cz.clovekvtisni.coordinator.android.event;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.util.BetterArrayAdapter;
import cz.clovekvtisni.coordinator.android.util.FindView;
import cz.clovekvtisni.coordinator.domain.Poi;

public class InfoFragment extends SherlockFragment {

	private static final String ARGS_KEY_EVENT_INFO = "eventInfo";

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
		View view = inflater.inflate(R.layout.frag_event_info, container, false);

		FindView.textView(view, R.id.info).setText(getArguments().getString(ARGS_KEY_EVENT_INFO));

		adapter = new ImportantPoisAdapter();
		ListView listView = FindView.listView(view, R.id.list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				((EventActivity) getActivity()).showPoiOnMap(adapter.getItem(position));
			}
		});

		return view;
	}

	public void setImportantPois(List<Poi> importantPois) {
		adapter.clear();
		adapter.addAll(importantPois);
	}

	private class ImportantPoisAdapter extends BetterArrayAdapter<Poi> {

		public ImportantPoisAdapter() {
			super(getActivity(), R.layout.item_with_icon);
		}

		@Override
		protected void setUpView(Poi poi, View view) {
			FindView.textView(view, R.id.title).setText(poi.getName());
			FindView.textView(view, R.id.short_description).setText(poi.getDescription());
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
