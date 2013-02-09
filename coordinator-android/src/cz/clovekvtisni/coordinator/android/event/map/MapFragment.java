package cz.clovekvtisni.coordinator.android.event.map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.actionbarsherlock.app.SherlockFragment;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.event.LocationTool;
import cz.clovekvtisni.coordinator.android.event.map.Projection.LatLon;
import cz.clovekvtisni.coordinator.android.util.Utils;

public class MapFragment extends SherlockFragment implements LocationTool.BestLocationListener {

	private LocationTool locationTool;
	private OsmMapView osmMapView;
	private MyLocationOverlay myLocationOverlay;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_map, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		osmMapView = (OsmMapView) view.findViewById(R.id.map);

		myLocationOverlay = new MyLocationOverlay(getResources());
		myLocationOverlay.setLatLon(new LatLon(50.083333, 14.416667));
		myLocationOverlay.setAccuracyMeters(100);
		osmMapView.addOverlay(myLocationOverlay);

		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.map_marker);
		osmMapView.addOverlay(new MarkerOverlay(new LatLon(50.1, 14.416667), b));

		locationTool = new LocationTool(this, getActivity());

		view.findViewById(R.id.goToMyLocation).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Location l = locationTool.getCurrentBestLocation();
				osmMapView.setCenter(new LatLon(l.getLatitude(), l.getLongitude()));
				osmMapView.setZoom(5000);
			}
		});
		
		view.findViewById(R.id.closeInfo).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hidePoiInfo();
			}
		});
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

	@Override
	public void onBestLocationUpdated(Location location) {
		myLocationOverlay.setAccuracyMeters(location.getAccuracy());
		myLocationOverlay.setLatLon(new LatLon(location.getLatitude(), location.getLongitude()));
		osmMapView.invalidate();
	}
	
	private void hidePoiInfo() {
		getView().findViewById(R.id.poiInfo).setVisibility(View.GONE);
	}
	
	private void showPoiInfo() {
		getView().findViewById(R.id.poiInfo).setVisibility(View.VISIBLE);
	}
	
	private class MarkerOverlay extends MapOverlay {
		private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		private final Bitmap bitmap;

		public MarkerOverlay(LatLon latLon, Bitmap bitmap) {
			super(latLon);
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
			showPoiInfo();
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