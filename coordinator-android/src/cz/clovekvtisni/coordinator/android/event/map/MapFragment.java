package cz.clovekvtisni.coordinator.android.event.map;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.event.EventActivity;
import cz.clovekvtisni.coordinator.android.event.LocationTool;
import cz.clovekvtisni.coordinator.android.event.map.Projection.LatLon;
import cz.clovekvtisni.coordinator.android.util.Utils;
import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.domain.config.PoiCategory;
import cz.clovekvtisni.coordinator.domain.config.WorkflowTransition;

public class MapFragment extends SherlockFragment implements LocationTool.BestLocationListener {

	private LocationTool locationTool;
	private OsmMapView osmMapView;
	private MyLocationOverlay myLocationOverlay;
	private View poiInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.frag_map, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		osmMapView = (OsmMapView) view.findViewById(R.id.map);

		myLocationOverlay = new MyLocationOverlay(getResources());
		myLocationOverlay.setLatLon(new LatLon(50.083333, 14.416667));
		myLocationOverlay.setAccuracyMeters(100);
		osmMapView.getOverlays().add(myLocationOverlay);

		locationTool = new LocationTool(this, getActivity());

		initPoiInfo();
	}

	@Override
	public void onResume() {
		super.onResume();
		locationTool.start();
	}

	@Override
	public void onPause() {
		super.onPause();
		locationTool.stop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		osmMapView.onDestroy();
	}

	private void initPoiInfo() {
		poiInfo = getView().findViewById(R.id.poiInfo);

		poiInfo.findViewById(R.id.closeInfo).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				poiInfo.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void onBestLocationUpdated(Location location) {
		myLocationOverlay.setAccuracyMeters(location.getAccuracy());
		myLocationOverlay.setLatLon(new LatLon(location.getLatitude(), location.getLongitude()));
		osmMapView.invalidate();
	}

	private void goToMyLocation() {
		Location l = locationTool.getCurrentBestLocation();
		osmMapView.setCenter(new LatLon(l.getLatitude(), l.getLongitude()));
		osmMapView.setZoom(5000);
	}

	private void showPoiInfo(Poi poi) {
		poiInfo.setVisibility(View.VISIBLE);

		TextView title = (TextView) poiInfo.findViewById(R.id.poiTitle);
		title.setText(poi.getName());
		
		TextView description = (TextView) poiInfo.findViewById(R.id.poiDescription);
		description.setText(poi.getDescription());

		LinearLayout layout = (LinearLayout) poiInfo.findViewById(R.id.transitions);
		layout.removeAllViews();
		for (WorkflowTransition t : poi.getWorkflowState().getTransitions()) {
			Button button = new Button(getActivity());
			button.setText(t.getName());
			layout.addView(button);
		}
	}

	public void setFilteredPois(List<Poi> pois, Map<PoiCategory, Bitmap> poiIcons) {
		List<MapOverlay> overlays = osmMapView.getOverlays();
		for (Iterator<MapOverlay> iter = overlays.iterator(); iter.hasNext();) {
			if (iter.next() instanceof MarkerOverlay) iter.remove();
		}

		for (Poi poi : pois) {
			Bitmap bitmap = poiIcons.get(poi.getPoiCategory());
			if (bitmap == null) continue;
			overlays.add(new MarkerOverlay(poi, bitmap));
		}

		osmMapView.invalidate();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.map, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_my_location:
			goToMyLocation();
			break;
		case R.id.menu_add_place:
			goToMyLocation();
			break;
		case R.id.menu_filter_places:
			((EventActivity) getActivity()).showPoiFilterDialog();
			break;
		case R.id.menu_filter_people:
			goToMyLocation();
			break;
		}
		return true;
	}

	public void showPoiOnMap(Poi poi) {
		osmMapView.setCenter(new LatLon(poi.getLatitude(), poi.getLongitude()));
		showPoiInfo(poi);
	}

	private class MarkerOverlay extends MapOverlay {
		private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		private final Poi poi;
		private final Bitmap bitmap;

		public MarkerOverlay(Poi poi, Bitmap bitmap) {
			super(new LatLon(poi.getLatitude(), poi.getLongitude()));
			this.poi = poi;
			this.bitmap = bitmap;
		}

		@Override
		public void onDraw(Canvas canvas, int x, int y, double oneMapMeterInPixels) {
			Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

			int left = x - bitmap.getWidth() / 2;
			Rect dst = new Rect(left, y - bitmap.getHeight(), left + bitmap.getWidth(), y);

			canvas.drawBitmap(bitmap, src, dst, paint);
		}

		@Override
		public void onTap() {
			showPoiInfo(poi);
		}
	}

	private static class MyLocationOverlay extends MapOverlay {
		private static final int COLOR_FILL = 0x330099CC;
		private static final int COLOR_STROKE = 0x990099CC;
		private static final int STROKE_WIDTH_DP = 1;
		private static final Paint PAINT_FILL;
		private static final Paint PAINT_STROKE;

		private double accuracyMeters;

		static {
			PAINT_FILL = new Paint();
			PAINT_FILL.setColor(COLOR_FILL);
			PAINT_FILL.setStyle(Style.FILL);
			PAINT_STROKE = new Paint();
			PAINT_STROKE.setColor(COLOR_STROKE);
			PAINT_STROKE.setStyle(Style.STROKE);
			PAINT_STROKE.setStrokeWidth(3);
		}

		public MyLocationOverlay(Resources res) {
			PAINT_STROKE.setStrokeWidth(Utils.dpToPx(res, STROKE_WIDTH_DP));
		}

		@Override
		public void onDraw(Canvas canvas, int x, int y, double oneMapMeterInPixels) {
			int radius = (int) (oneMapMeterInPixels * accuracyMeters);
			canvas.drawCircle(x, y, radius, PAINT_FILL);
			canvas.drawCircle(x, y, radius, PAINT_STROKE);
		}

		public void setAccuracyMeters(double accuracyMeters) {
			this.accuracyMeters = accuracyMeters;
		}
	}

}