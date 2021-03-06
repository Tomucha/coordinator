package cz.clovekvtisni.coordinator.android.event;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.fhucho.android.workers.Workers;
import com.fhucho.android.workers.simple.ActivityWorker2;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ApiCall.ApiCallException;
import cz.clovekvtisni.coordinator.android.api.ApiCalls;
import cz.clovekvtisni.coordinator.android.api.ApiCalls.EventPoiTransitionCall;
import cz.clovekvtisni.coordinator.android.event.map.MapOverlay;
import cz.clovekvtisni.coordinator.android.event.map.view.NetworkTileLoader;
import cz.clovekvtisni.coordinator.android.event.map.view.OsmMapView;
import cz.clovekvtisni.coordinator.android.event.map.view.Projection.LatLon;
import cz.clovekvtisni.coordinator.android.other.Settings;
import cz.clovekvtisni.coordinator.android.util.*;
import cz.clovekvtisni.coordinator.api.request.EventPoiRequestParams;
import cz.clovekvtisni.coordinator.api.request.EventPoiTransitionRequestParams;
import cz.clovekvtisni.coordinator.api.response.EventPoiResponseData;
import cz.clovekvtisni.coordinator.domain.EventLocation;
import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.domain.config.PoiCategory;
import cz.clovekvtisni.coordinator.domain.config.SubCategory;
import cz.clovekvtisni.coordinator.domain.config.WorkflowState;
import cz.clovekvtisni.coordinator.domain.config.WorkflowTransition;

public class MapFragment extends SherlockFragment implements OsmMapView.OsmMapEventsListener {

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
        new TransitionPoiDialog(params, poi, transition).show(getActivity().getSupportFragmentManager(), TransitionPoiDialog.TAG);

        /*

		Workers.start(new EventPoiTransitionTask(params, transition), (EventActivity) getActivity());
		new SendingProgressDialog().show(getActivity().getSupportFragmentManager(), SendingProgressDialog.TAG);
		poiInfo.setVisibility(View.GONE);
		*/
	}

    public class TransitionPoiDialog extends DialogFragment implements OnClickListener {

        private static final String TAG = "TransitionPoiDialog";

        EventPoiTransitionRequestParams requestParams = null;
        Poi poi = null;
        WorkflowTransition transition = null;

        public TransitionPoiDialog(EventPoiTransitionRequestParams requestParams, Poi poi, WorkflowTransition transition) {
            this.requestParams = requestParams;
            this.poi = poi;
            this.transition = transition;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_transition_poi, container);
            getDialog().setTitle("Posun POI ve workflow");
            view.findViewById(R.id.button).setOnClickListener(this);
            ((TextView)view.findViewById(R.id.poi_name)).setText(poi.getName());
            ((TextView)view.findViewById(R.id.transition_name)).setText(transition.getName());
            return view;
        }

        @Override
        public void onClick(View view) {
            View root = getView();

            String comment = ((EditText)root.findViewById(R.id.comment)).getText().toString().trim();
            requestParams.setComment(comment);

            Workers.start(new EventPoiTransitionTask(requestParams, transition), (EventActivity) getActivity());
            new SendingProgressDialog().show(getActivity().getSupportFragmentManager(), SendingProgressDialog.TAG);
            poiInfo.setVisibility(View.GONE);
            dismiss();
        }
    }

	private void goToMyLocation() {
        if (myLocation != null) {
		    LatLon latLon = new LatLon(myLocation.getLatitude(), myLocation.getLongitude());
		    osmMapView.setCenter(latLon);
		    osmMapView.setZoom(5000);
        } else {
            UiTool.toast(R.string.message_your_position_unknown, getActivity().getApplicationContext());
        }
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
        osmMapView.setOsmMapEventsListener(this);

		myLocationOverlay = new MyLocationOverlay(getResources());
		osmMapView.getOverlays().add(myLocationOverlay);
		updateLocationOverlay();

		initPoiInfo(view);

        centerMapOnEvent(((EventActivity)getActivity()).getEvent().getLocationList());

        ((EventActivity) getActivity()).onMapFragmentReady();

		return view;
	}

    private void centerMapOnEvent(EventLocation[] locationList) {
        if (Settings.hasMapSettings(getEventId())) {
            Lg.MAP.i("Loading position from settings");
            // let's zoom where we have been last time
            float latitude = Settings.getMapSettingsLatitude(getEventId());
            float longitude = Settings.getMapSettingsLongitude(getEventId());
            float zoom = Settings.getMapSettingsZoom(getEventId());
            osmMapView.setCenter(new LatLon(latitude, longitude));
            osmMapView.setZoom(zoom);
            return;
        }
        if (locationList == null || locationList.length == 0) {
            return;
        }
        Lg.MAP.i("Loading position from "+locationList[0]);
        osmMapView.setCenter(new LatLon(locationList[0].getLatitude(), locationList[0].getLongitude()));
    }

    private long getEventId() {
        return ((EventActivity)getActivity()).getEventId();
    }

    @Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_my_location:
			goToMyLocation();
			break;
		case R.id.menu_filter_places:
			((EventActivity) getActivity()).showPoiFilterDialog();
			break;
		case R.id.menu_filter_people:
            ((EventActivity) getActivity()).showPeopleFilterDialog();
			break;
		case R.id.menu_sync:
			Lg.MAP.i("Reload!");
			((EventActivity)getActivity()).loadPois(true);
			break;
		}
		return true;
	}

	private void selectMarker(MarkerOverlay marker) {
		if (selectedMarker != null) selectedMarker.setSelected(false);
        osmMapView.invalidate();
        if (marker == null) return;
		marker.setSelected(true);
		selectedMarker = marker;
	}

	private PoiOverlay findPoiOverlay(Poi poi) {
		for (MapOverlay overlay : osmMapView.getOverlays()) {
			if (overlay instanceof PoiOverlay) {
				PoiOverlay poiOverlay = (PoiOverlay) overlay;
				if (poiOverlay.poi.getId().equals(poi.getId())) {
					return poiOverlay;
				}
			}
		}
        return null;
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
        return null;
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
			Bitmap bitmap = poiIcons.get(findPoiCategory(poi));
			if (bitmap == null) {
                Lg.MAP.e("Missing icon for: "+findPoiCategory(poi)+" in "+poiIcons.keySet());
                continue;
            }

            // badge?
            if (poi.isCanDoTransition()) {
			    overlays.add(new PoiOverlay(poi, bitmap,R.drawable.ic_work_badge));
            } else {
                overlays.add(new PoiOverlay(poi, bitmap,null));
            }
		}

        if (toZoomTo != null) {
            Lg.APP.i("Zooming to "+toZoomTo);
            showPoiOnMap(toZoomTo);
        }

		osmMapView.invalidate();
	}

    private PoiCategory findPoiCategory(Poi poi) {
        return ((EventActivity)getActivity()).findPoiCategory(poi.getPoiCategoryId());
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
        WorkflowState workflowState = findWorkflowState(poi);

		if (workflowState != null && poi.isCanDoTransition()) {
			for (final WorkflowTransition transition : workflowState.getTransitions()) {
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

        poiInfo.findViewById(R.id.navigation).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myLocation != null) {
                    String url = "http://maps.google.com/maps?saddr="+myLocation.getLatitude()+","+myLocation.getLongitude()+"&daddr="+poi.getLatitude()+","+poi.getLongitude();
                    Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    navIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(navIntent);
                }
            }
        });

        poiInfo.findViewById(R.id.editInfo).setVisibility(findPoiCategory(poi).getSubCategories().isEmpty() ? View.GONE : View.VISIBLE);
        poiInfo.findViewById(R.id.editInfo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditSubcategoryDialog(poi);
            }
        }
        );
    }

    private void showEditSubcategoryDialog(final Poi poi) {
        PoiCategory poiCategory = findPoiCategory(poi);
        final List<SubCategory> subs = poiCategory.getSubCategories();
        String[] subNames = new String[subs.size()];
        for (int i = 0; i < subNames.length; i++) {
            subNames[i] = subs.get(i).getName();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_poi_edit))
            .setItems(subNames, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    EventPoiRequestParams params = new EventPoiRequestParams();
                    params.setEventId(poi.getEventId());
                    params.setPoiId(poi.getId());
                    params.setPoiSubCategoryId(subs.get(which).getId());
                    Workers.start(new EventPoiUpdateTask(params), (EventActivity) getActivity());
                    new SendingProgressDialog().show(getActivity().getSupportFragmentManager(), SendingProgressDialog.TAG);

                }
            });
        builder.create().show();
    }

    private WorkflowState findWorkflowState(Poi poi) {
        return ((EventActivity)getActivity()).findWorkflowState(poi);
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

    public void saveMapSettings() {
        Settings.setMapSettings(getEventId(),
                (float)osmMapView.getCenter().getLat(),
                (float)osmMapView.getCenter().getLon(),
                (float)osmMapView.getZoom()
        );
    }

    @Override
    public void onLongTap(double latitude, double longitude) {
        // let's create a new POI
        PoiCategory[] poiCategories = ((EventActivity)getActivity()).getUserInEvent().getOpenedCategories();
        if (poiCategories == null || poiCategories.length == 0) return;
        new CreatePoiDialog(latitude, longitude, poiCategories).show(getActivity().getSupportFragmentManager(), CreatePoiDialog.TAG);
    }

    private static class EventPoiTransitionTask extends ActivityWorker2<EventActivity, EventPoiResponseData, Exception> {

		private final EventPoiTransitionRequestParams params;
        private WorkflowTransition transition;

		public EventPoiTransitionTask(EventPoiTransitionRequestParams params, WorkflowTransition transition) {
			this.params = params;
            this.transition = transition;
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
            UiTool.toast(R.string.error_server, getActivity().getApplicationContext());
            FragmentManager fm = getActivity().getSupportFragmentManager();
            ((DialogFragment) fm.findFragmentByTag(SendingProgressDialog.TAG)).dismiss();
		}

		@Override
		public void onSuccess(EventPoiResponseData result) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			((DialogFragment) fm.findFragmentByTag(SendingProgressDialog.TAG)).dismiss();
            ((EventActivity)getActivity()).loadPois(true);

            UiTool.toast(R.string.transition_finished, getActivity());

            Lg.API.i("Transition success: "+ transition.getIntentPackage()+" "+ transition.getIntentClass());

            final WorkflowTransition t = transition;
            {
                if (t.getIntentPackage() != null && t.getIntentClass() != null) {
                    Intent toRunAfter = Intent.makeMainActivity(new ComponentName(t.getIntentPackage(), t.getIntentClass()));
                    Lg.APP.i("Starting Intent: " + toRunAfter);
                    getActivity().startActivity(toRunAfter);
                }
            }


		}

	}

    private static class EventPoiCreateTask extends ActivityWorker2<EventActivity, EventPoiResponseData, Exception> {

        private final EventPoiRequestParams params;

        public EventPoiCreateTask(EventPoiRequestParams params) {
            this.params = params;
        }

        @Override
        protected void doInBackground() {
            try {
                getListenerProxy().onSuccess(new ApiCalls.EventPoiCreateCall(params).call());
            } catch (ApiCallException e) {
                getListenerProxy().onException(e);
            }
        }

        @Override
        public void onException(Exception e) {
            Lg.API.e("Exception calling API: "+e, e);
            UiTool.toast(R.string.error_server, getActivity().getApplicationContext());
            FragmentManager fm = getActivity().getSupportFragmentManager();
            ((DialogFragment) fm.findFragmentByTag(SendingProgressDialog.TAG)).dismiss();
        }

        @Override
        public void onSuccess(EventPoiResponseData result) {
            UiTool.toast(R.string.ok_poi_saved, getActivity().getApplicationContext());
            FragmentManager fm = getActivity().getSupportFragmentManager();
            ((DialogFragment) fm.findFragmentByTag(SendingProgressDialog.TAG)).dismiss();
            ((EventActivity)getActivity()).loadPois(true);
        }

    }

    private static class EventPoiUpdateTask extends ActivityWorker2<EventActivity, EventPoiResponseData, Exception> {

        private final EventPoiRequestParams params;

        public EventPoiUpdateTask(EventPoiRequestParams params) {
            this.params = params;
        }

        @Override
        protected void doInBackground() {
            try {
                getListenerProxy().onSuccess(new ApiCalls.EventPoiUpdateCall(params).call());
            } catch (ApiCallException e) {
                getListenerProxy().onException(e);
            }
        }

        @Override
        public void onException(Exception e) {
            Lg.API.e("Exception calling API: "+e, e);
            UiTool.toast(R.string.error_server, getActivity().getApplicationContext());
            FragmentManager fm = getActivity().getSupportFragmentManager();
            ((DialogFragment) fm.findFragmentByTag(SendingProgressDialog.TAG)).dismiss();
        }

        @Override
        public void onSuccess(EventPoiResponseData result) {
            UiTool.toast(R.string.ok_poi_saved, getActivity().getApplicationContext());
            FragmentManager fm = getActivity().getSupportFragmentManager();
            ((DialogFragment) fm.findFragmentByTag(SendingProgressDialog.TAG)).dismiss();
            ((EventActivity)getActivity()).loadPois(true);
        }

    }

	private class MarkerOverlay extends MapOverlay {
		private final Paint paintNormal = new Paint(Paint.FILTER_BITMAP_FLAG);
		private final Paint paintSelected = new Paint(paintNormal);
		private final Bitmap bitmap;
        private Drawable badgeDrawable;

        private boolean selected = false;

		public MarkerOverlay(LatLon latLon, Bitmap bitmap, Integer badge) {
			super(latLon);
			this.bitmap = bitmap;
			paintSelected.setColor(Color.RED);
            paintSelected.setShadowLayer(4,2,2, Color.BLACK);
            paintSelected.setAntiAlias(true);

            if (badge != null) {
                badgeDrawable = getResources().getDrawable(badge);
            }
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

            if (badgeDrawable != null) {
                badgeDrawable.setBounds(x, y - bitmap.getHeight()/2, x + bitmap.getHeight()/2, y);
                badgeDrawable.draw(canvas);
            }

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

		public PoiOverlay(Poi poi, Bitmap bitmap, Integer badge) {
			super(new LatLon(poi.getLatitude(), poi.getLongitude()), bitmap, badge);
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
					userInEvent.getLastLocationLongitude()), userMarkerBitmap, null);
			this.userInEvent = userInEvent;
		}

		@Override
		public void onTap() {
			selectUser(userInEvent.getUser());
		}
	}

	public static class SendingProgressDialog extends DialogFragment {
		private static final String TAG = "SendingProgressDialog";

		@Override
		public Dialog onCreateDialog(Bundle state) {
			final ProgressDialog dialog = new ProgressDialog(getActivity());
			dialog.setMessage("Odesílám...");
			dialog.setCancelable(false);
			return dialog;
		}
	}

    /**
     * New POI dialog.
     *
     * User: tomucha
     * Date: 31.03.13
     */
    public class CreatePoiDialog extends DialogFragment implements OnClickListener {

        private static final String TAG = "CreatePoiDialog";

        private double latitude;
        private double longitude;
        private final PoiCategory[] poiCategories;

        public CreatePoiDialog(double latitude, double longitude, PoiCategory[] poiCategories) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.poiCategories = poiCategories;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_create_poi, container);
            getDialog().setTitle("Nové POI");

            Spinner s = (Spinner) view.findViewById(R.id.poi_categories);
            s.setAdapter(new PoiCategoryAdapter(poiCategories));

            view.findViewById(R.id.button).setOnClickListener(this);

            return view;
        }

        @Override
        public void onClick(View view) {
            View root = getView();
            PoiCategory category = (PoiCategory) ((Spinner)root.findViewById(R.id.poi_categories)).getSelectedItem();
            String name = ((TextView)root.findViewById(R.id.poi_name)).getText().toString().trim();
            String description = ((TextView)root.findViewById(R.id.poi_description)).getText().toString().trim();
            Spinner s = (Spinner) root.findViewById(R.id.poi_categories);

            if (name.length() < 3) {
                ((TextView)root.findViewById(R.id.poi_name)).requestFocus();
            } else {

                EventPoiRequestParams params = new EventPoiRequestParams();
                params.setName(name);
                params.setDescription(description);
                params.setEventId(((EventActivity) getActivity()).getEventId());
                params.setLatitude(latitude);
                params.setLongitude(longitude);

                params.setPoiCategoryId(((PoiCategory)s.getSelectedItem()).getId());

                Workers.start(new EventPoiCreateTask(params), (EventActivity) getActivity());

                new SendingProgressDialog().show(getActivity().getSupportFragmentManager(), SendingProgressDialog.TAG);

                dismiss();

            }
        }
    }

    private class PoiCategoryAdapter extends BetterArrayAdapter<PoiCategory> {
        public PoiCategoryAdapter(PoiCategory[] poiCategories) {
            super(getActivity(), R.layout.item_poi_category);
            addAll(poiCategories);
        }
        @Override
        protected void setUpView(PoiCategory poiCategory, View view) {
            FindView.textView(view, R.id.name).setText(poiCategory.getName());
            FindView.textView(view, R.id.description).setText(poiCategory.getDescription().trim());
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