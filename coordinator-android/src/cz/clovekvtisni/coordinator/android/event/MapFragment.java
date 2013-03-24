package cz.clovekvtisni.coordinator.android.event;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
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
import com.fhucho.android.workers.Workers;
import com.fhucho.android.workers.simple.ActivityWorker2;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ApiCall.ApiCallException;
import cz.clovekvtisni.coordinator.android.api.ApiCalls.EventPoiTransitionCall;
import cz.clovekvtisni.coordinator.android.event.map.MapOverlay;
import cz.clovekvtisni.coordinator.android.event.map.view.NetworkTileLoader;
import cz.clovekvtisni.coordinator.android.event.map.view.OsmMapView;
import cz.clovekvtisni.coordinator.android.event.map.view.Projection.LatLon;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.android.util.Utils;
import cz.clovekvtisni.coordinator.api.request.EventPoiTransitionRequestParams;
import cz.clovekvtisni.coordinator.api.response.EventPoiResponseData;
import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.domain.config.PoiCategory;
import cz.clovekvtisni.coordinator.domain.config.WorkflowTransition;

public class MapFragment extends SherlockFragment {

	private Bitmap userMarkerBitmap;
	private Location myLocation;
	private OsmMapView osmMapView;
	private MarkerOverlay selectedMarker;
	private MyLocationOverlay myLocationOverlay;
	private View poiInfo;
    private Long zoomToPoi = null;

    private void closeMarkerInfo() {
		poiInfo.setVisibility(View.GONE);
		selectedMarker.setSelected(false);
		selectedMarker = null;
		osmMapView.invalidate();
	}

	private void doPoiTransition(Poi poi, WorkflowTransition transition) {
		EventPoiTransitionRequestParams params = new EventPoiTransitionRequestParams();
		params.setEventId(poi.getEventId());
		params.setPoiId(poi.getId());
		params.setTransitionId(transition.getId());

		Workers.start(new EventPoiTransitionTask(params), (EventActivity) getActivity());

		new SendingTransitionDialog().show(getActivity().getSupportFragmentManager(),
				SendingTransitionDialog.TAG);

		poiInfo.setVisibility(View.GONE);
	}

	private void goToMyLocation() {
		LatLon latLon = new LatLon(myLocation.getLatitude(), myLocation.getLongitude());
		osmMapView.setCenter(latLon);
		osmMapView.setZoom(5000);
	}

	private void initPoiInfo(View view) {
		poiInfo = view.findViewById(R.id.poiInfo);

		poiInfo.findViewById(R.id.closeInfo).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeMarkerInfo();
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.map, menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_event_map, container, false);

		setHasOptionsMenu(true);

		userMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker_user);

		osmMapView = (OsmMapView) view.findViewById(R.id.map);

		myLocationOverlay = new MyLocationOverlay(getResources());
		osmMapView.getOverlays().add(myLocationOverlay);
		updateLocationOverlay();

		initPoiInfo(view);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		((EventActivity) getActivity()).onMapFragmentReady();
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

	private void selectMarker(MarkerOverlay marker) {
		if (selectedMarker != null) selectedMarker.setSelected(false);
		marker.setSelected(true);
		selectedMarker = marker;
		osmMapView.invalidate();
	}

	private PoiOverlay findPoiOverlay(Poi poi) {
		for (MapOverlay overlay : osmMapView.getOverlays()) {
			if (overlay instanceof PoiOverlay) {
				PoiOverlay poiOverlay = (PoiOverlay) overlay;
				if (poiOverlay.poi.getId() == poi.getId()) {
					return poiOverlay;
				}
			}
		}

		throw new AssertionError();
	}

	private UserOverlay findUserOverlay(User user) {
		for (MapOverlay overlay : osmMapView.getOverlays()) {
			if (overlay instanceof UserOverlay) {
				UserOverlay userOverlay = (UserOverlay) overlay;
				if (userOverlay.userInEvent.getUser().getId() == user.getId()) {
					return userOverlay;
				}
			}
		}

		throw new AssertionError();
	}

	private void selectPoi(Poi poi) {
		selectMarker(findPoiOverlay(poi));
		showPoiInfo(poi);
	}

	private void selectUser(User user) {
		selectMarker(findUserOverlay(user));
		showUserInfo(user);
	}

	public void setFilteredPois(List<Poi> pois, Map<PoiCategory, Bitmap> poiIcons) {
        Lg.APP.i("Setting filtered POIs to MapFragment: "+pois.size());
		List<MapOverlay> overlays = osmMapView.getOverlays();
		for (Iterator<MapOverlay> iter = overlays.iterator(); iter.hasNext();) {
			if (iter.next() instanceof PoiOverlay) iter.remove();
		}

        Poi toZoomTo = null;

		for (Poi poi : pois) {
            if (zoomToPoi != null) {
                if (poi.getId() == zoomToPoi.longValue()) {
                    toZoomTo = poi;
                    zoomToPoi = null;
                }
            }
			Bitmap bitmap = poiIcons.get(poi.getPoiCategory());
			if (bitmap == null) continue;
			overlays.add(new PoiOverlay(poi, bitmap));
		}

        if (toZoomTo != null) {
            Lg.APP.i("Zooming to "+toZoomTo);
            showPoiOnMap(toZoomTo);
        }

		osmMapView.invalidate();
	}

	public void setFilteredUsers(List<UserInEvent> users) {
        if (osmMapView == null) {
            return;
        }
		List<MapOverlay> overlays = osmMapView.getOverlays();
		for (Iterator<MapOverlay> iter = overlays.iterator(); iter.hasNext();) {
			if (iter.next() instanceof UserOverlay) iter.remove();
		}

		for (UserInEvent user : users) {
			if (user.getLastLocationLatitude() != null) overlays.add(new UserOverlay(user));
		}

		osmMapView.invalidate();
	}

	public void setMyLocation(Location myLocation) {
		this.myLocation = myLocation;

		// If the view has been created
		if (isVisible()) updateLocationOverlay();
	}
	
	public void setNetTileLoader(NetworkTileLoader netTileLoader) {
		osmMapView.setNetTileLoader(netTileLoader);
	}

	private void showPoiInfo(final Poi poi) {
		poiInfo.setVisibility(View.VISIBLE);

		TextView title = (TextView) poiInfo.findViewById(R.id.poiTitle);
		title.setText(poi.getName());

		TextView description = (TextView) poiInfo.findViewById(R.id.poiDescription);
		description.setText(poi.getDescription());

		LinearLayout layout = (LinearLayout) poiInfo.findViewById(R.id.transitions);
		layout.removeAllViews();
		if (poi.getWorkflowState() != null) {
			for (final WorkflowTransition transition : poi.getWorkflowState().getTransitions()) {
				Button button = new Button(getActivity());
				button.setText(transition.getName());
				layout.addView(button);

				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						doPoiTransition(poi, transition);
					}
				});
			}
		}
	}

	public void showPoiOnMap(Poi poi) {
		LatLon latLon = new LatLon(poi.getLatitude(), poi.getLongitude());
		osmMapView.setCenter(latLon);
		selectPoi(poi);
	}

	private void showUserInfo(User user) {
		poiInfo.setVisibility(View.VISIBLE);

		TextView title = (TextView) poiInfo.findViewById(R.id.poiTitle);
		title.setText(user.getFullName());

		TextView description = (TextView) poiInfo.findViewById(R.id.poiDescription);
		description.setText(user.getCity());

		LinearLayout layout = (LinearLayout) poiInfo.findViewById(R.id.transitions);
		layout.removeAllViews();
	}

	public void showUserOnMap(UserInEvent user) {
		LatLon latLon = new LatLon(user.getLastLocationLatitude(), user.getLastLocationLongitude());
		osmMapView.setCenter(latLon);
		selectUser(user.getUser());
	}

	private void updateLocationOverlay() {
		if (myLocation == null) return;
		myLocationOverlay.setAccuracyMeters(myLocation.getAccuracy());
		myLocationOverlay
				.setLatLon(new LatLon(myLocation.getLatitude(), myLocation.getLongitude()));
		myLocationOverlay.setVisible();
		osmMapView.invalidate();
	}

    public void zoomToPoi(Long zoomToPoi) {
        this.zoomToPoi = zoomToPoi;
    }

    private static class EventPoiTransitionTask extends
			ActivityWorker2<EventActivity, EventPoiResponseData, Exception> {

		private final EventPoiTransitionRequestParams params;

		public EventPoiTransitionTask(EventPoiTransitionRequestParams params) {
			this.params = params;
		}

		@Override
		protected void doInBackground() {
			try {
				getListenerProxy().onSuccess(new EventPoiTransitionCall(params).call());
			} catch (ApiCallException e) {
				getListenerProxy().onException(e);
			}
		}

		@Override
		public void onException(Exception e) {
		}

		@Override
		public void onSuccess(EventPoiResponseData result) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			((DialogFragment) fm.findFragmentByTag(SendingTransitionDialog.TAG)).dismiss();
		}

	}

	private class MarkerOverlay extends MapOverlay {
		private final Paint paintNormal = new Paint(Paint.FILTER_BITMAP_FLAG);
		private final Paint paintSelected = new Paint(paintNormal);
		private final Bitmap bitmap;

		private boolean selected = false;

		public MarkerOverlay(LatLon latLon, Bitmap bitmap) {
			super(latLon);
			this.bitmap = bitmap;
			paintSelected.setColor(Color.RED);
            paintSelected.setShadowLayer(4,2,2, Color.BLACK);
            paintSelected.setAntiAlias(true);
		}

		@Override
		public void onDraw(Canvas canvas, int x, int y, double oneMapMeterInPixels) {
			Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

			int left = x - bitmap.getWidth() / 2;
			Rect dst = new Rect(left, y - bitmap.getHeight(), left + bitmap.getWidth(), y);

            if (selected) {
                canvas.drawCircle(x, y - (bitmap.getHeight()/2) , bitmap.getWidth() * 0.6f, paintSelected);
            }

			canvas.drawBitmap(bitmap, src, dst, paintNormal);
		}

		private void setSelected(boolean selected) {
			this.selected = selected;
		}
	}

	private static class MyLocationOverlay extends MapOverlay {
		private static final int COLOR_FILL = 0x330099CC;
		private static final int COLOR_STROKE = 0x990099CC;
		private static final int STROKE_WIDTH_DP = 1;
		private static final Paint PAINT_FILL;
		private static final Paint PAINT_STROKE;

		private boolean visible = false;
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
			if (!visible) return;
			int radius = (int) (oneMapMeterInPixels * accuracyMeters);
			canvas.drawCircle(x, y, radius, PAINT_FILL);
			canvas.drawCircle(x, y, radius, PAINT_STROKE);
		}

		public void setAccuracyMeters(double accuracyMeters) {
			this.accuracyMeters = accuracyMeters;
		}

		public void setVisible() {
			visible = true;
		}
	}

	private class PoiOverlay extends MarkerOverlay {
		private final Poi poi;

		public PoiOverlay(Poi poi, Bitmap bitmap) {
			super(new LatLon(poi.getLatitude(), poi.getLongitude()), bitmap);
			this.poi = poi;
		}

		@Override
		public void onTap() {
			selectPoi(poi);
		}
	}

	private class UserOverlay extends MarkerOverlay {
		private final UserInEvent userInEvent;

		public UserOverlay(UserInEvent userInEvent) {
			super(new LatLon(userInEvent.getLastLocationLatitude(),
					userInEvent.getLastLocationLongitude()), userMarkerBitmap);
			this.userInEvent = userInEvent;
		}

		@Override
		public void onTap() {
			selectUser(userInEvent.getUser());
		}
	}

	public static class SendingTransitionDialog extends DialogFragment {
		private static final String TAG = "SendingTransitionDialog";

		@Override
		public Dialog onCreateDialog(Bundle state) {
			final ProgressDialog dialog = new ProgressDialog(getActivity());
			dialog.setMessage("Odesílám...");
			dialog.setCancelable(false);
			return dialog;
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