package cz.clovekvtisni.coordinator.android.event.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class MapFragment extends SherlockFragment {

	private OsmMapView osmMapView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		osmMapView = new OsmMapView(getActivity());
		return osmMapView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		osmMapView.onDestroy();
	}

}
