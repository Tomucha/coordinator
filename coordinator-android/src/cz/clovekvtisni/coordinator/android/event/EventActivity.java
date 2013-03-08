package cz.clovekvtisni.coordinator.android.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.fhucho.android.workers.Workers;
import com.fhucho.android.workers.simple.ActivityWorker2;
import com.google.common.collect.Lists;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ApiCall.ApiCallException;
import cz.clovekvtisni.coordinator.android.api.ApiCalls.UserUpdatePositionCall;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.ConfigLoader;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.ConfigLoaderListener;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.EventPoiListLoader;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.EventPoiListLoaderListener;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.EventUserListLoader;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.EventUserListLoaderListener;
import cz.clovekvtisni.coordinator.android.api.BitmapLoader;
import cz.clovekvtisni.coordinator.android.event.MapFragment.SendingTransitionDialog;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.android.util.SimpleListeners.SimpleTabListener;
import cz.clovekvtisni.coordinator.api.request.EventPoiListRequestParams;
import cz.clovekvtisni.coordinator.api.request.EventUserListRequestParams;
import cz.clovekvtisni.coordinator.api.request.UserUpdatePositionRequestParams;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
import cz.clovekvtisni.coordinator.api.response.EventPoiFilterResponseData;
import cz.clovekvtisni.coordinator.api.response.EventUserListResponseData;
import cz.clovekvtisni.coordinator.api.response.UserUpdatePositionResponseData;
import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.domain.config.PoiCategory;

public class EventActivity extends SherlockFragmentActivity implements LocationTool.Listener {

	private List<SherlockFragment> fragments;
	private LocationTool locationTool;
	private MapFragment mapFragment;
	private TasksFragment tasksFragment;
	private UsersFragment usersFragment;
	private ViewPager pager;

	private Map<PoiCategory, Boolean> poiFilter;
	private Map<PoiCategory, Bitmap> poiIcons = new HashMap<PoiCategory, Bitmap>();
	private Poi[] pois = new Poi[0];
	private PoiCategory[] poiCategories;

	private long getEventId() {
		return IntentHelper.getEvent(getIntent()).getId();
	}

	private void initFragments() {
		mapFragment = new MapFragment();
		tasksFragment = new TasksFragment();
		usersFragment = new UsersFragment();

		fragments = Lists.newArrayList(mapFragment, tasksFragment, usersFragment,
				new UsersFragment());
	}

	private void initPager() {
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setOffscreenPageLimit(3);
		pager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager()));
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				getSupportActionBar().setSelectedNavigationItem(position);
			}
		});
	}

	private void initTabs() {
		SimpleTabListener tabListener = new SimpleTabListener() {
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				pager.setCurrentItem(tab.getPosition());
			}
		};

		ActionBar bar = getSupportActionBar();
		bar.addTab(bar.newTab().setTabListener(tabListener).setText("Mapa"));
		bar.addTab(bar.newTab().setTabListener(tabListener).setText("Úkoly"));
		bar.addTab(bar.newTab().setTabListener(tabListener).setText("Lidé"));
		bar.addTab(bar.newTab().setTabListener(tabListener).setText("Info"));
	}

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_event);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		initFragments();
		initPager();
		initTabs();

		loadUsers();
		loadPoiCategories();

		locationTool = new LocationTool(this, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		locationTool.resume();
	}

	@Override
	public void onPause() {
		super.onPause();
		locationTool.pause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.event, menu);
		return true;
	}

	private void loadPois() {
		EventPoiListRequestParams params = new EventPoiListRequestParams();
		params.setEventId(getEventId());
		params.setModifiedFrom(new Date(0));
		Workers.load(new EventPoiListLoader(params), new EventPoiListLoaderListener() {
			@Override
			public void onResult(EventPoiFilterResponseData result) {
				pois = result.getPois();
				updatePois();
			}

			@Override
			public void onException(Exception e) {
				e.printStackTrace();
			}
		}, this);
	}

	private void loadPoiIcons() {
		for (final PoiCategory category : poiCategories) {
			String url = category.getIcon();
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			Workers.load(new BitmapLoader(url, metrics.densityDpi), new BitmapLoader.Listener() {
				@Override
				public void onSuccess(Bitmap bitmap) {
					poiIcons.put(category, bitmap);
					updatePois();
				}

				@Override
				public void onException(Exception e) {
					e.printStackTrace();
				}
			}, this);
		}
	}

	private void loadPoiCategories() {
		Workers.load(new ConfigLoader(), new ConfigLoaderListener() {
			@Override
			public void onResult(ConfigResponse result) {
				poiCategories = result.getPoiCategoryList();
				onPoiCategoriesLoaded(poiCategories);
			}

			@Override
			public void onException(Exception e) {
			}
		}, this);
	}

	private void loadUsers() {
		EventUserListRequestParams params = new EventUserListRequestParams();
		params.setEventId(getEventId());
		params.setModifiedFrom(new Date(0));
		Workers.load(new EventUserListLoader(params), new EventUserListLoaderListener() {
			@Override
			public void onResult(EventUserListResponseData result) {
				onUsersLoaded(result.getUserInEvents());
			}

			@Override
			public void onException(Exception e) {
			}
		}, this);
	}

	public void onPoiCategoriesLoaded(PoiCategory[] poiCategories) {
		this.poiCategories = poiCategories;
		this.poiFilter = new HashMap<PoiCategory, Boolean>();

		for (PoiCategory category : poiCategories) {
			poiFilter.put(category, true);
		}

		loadPois();
		loadPoiIcons();
	}

	private void onUsersLoaded(UserInEvent[] users) {
		List<UserInEvent> usersList = Lists.newArrayList(users);
		mapFragment.setFilteredUsers(usersList);
		usersFragment.setFilteredUsers(usersList);
	}

	private void updatePois() {
		List<Poi> filteredPois = new ArrayList<Poi>();
		for (Poi poi : pois) {
			if (poiFilter.get(poi.getPoiCategory())) filteredPois.add(poi);
		}

		mapFragment.setFilteredPois(filteredPois, poiIcons);
		tasksFragment.setFilteredPois(filteredPois);
	}

	public void showPoiFilterDialog() {
		new PoiFilterDialog().show(getSupportFragmentManager(), PoiFilterDialog.TAG);
	}

	public void showPoiOnMap(Poi poi) {
		mapFragment.showPoiOnMap(poi);
		pager.setCurrentItem(0, true);
	}

	public void showUserOnMap(UserInEvent user) {
		mapFragment.showUserOnMap(user);
		pager.setCurrentItem(0, true);
	}

	@Override
	public void onLocationUpdated(Location location) {
		Lg.LOCATION.d("Location updated. " + describeLocation(location));
		mapFragment.setMyLocation(location);
	}

	@Override
	public void onLocationShouldBeUploaded(Location location) {
		Lg.LOCATION.d("Location should be uploaded. " + describeLocation(location));
		Workers.start(new UploadMyLocationTask(location, getEventId()), this);
	}

	private String describeLocation(Location location) {
		return "Accuracy: " + location.getAccuracy() + " m";
	}

	public class TabsPagerAdapter extends FragmentPagerAdapter {
		public TabsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			return fragments.get(i);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}

	private static class UploadMyLocationTask extends
			ActivityWorker2<EventActivity, UserUpdatePositionResponseData, Exception> {

		private final long eventId;
		private final Location location;

		public UploadMyLocationTask(Location location, long eventId) {
			this.location = location;
			this.eventId = eventId;
		}

		@Override
		protected void doInBackground() {
			try {
				UserUpdatePositionRequestParams params = new UserUpdatePositionRequestParams();
				params.setEventId(eventId);
				params.setLatitude(location.getLatitude());
				params.setLongitude(location.getLongitude());
				getListenerProxy().onSuccess(new UserUpdatePositionCall(params).call());
			} catch (ApiCallException e) {
				getListenerProxy().onException(e);
			}
		}

		@Override
		public void onSuccess(UserUpdatePositionResponseData result) {
		}

		@Override
		public void onException(Exception e) {
		}

	}

	public static class PoiFilterDialog extends SherlockDialogFragment {
		private static final String TAG = "poi-filter-dialog";

		@Override
		public Dialog onCreateDialog(Bundle state) {
			EventActivity activity = (EventActivity) getActivity();

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Zobrazovat tyto místa");

			String items[] = extractNames(activity.poiCategories);
			boolean[] checkedItems = extractChecked(activity.poiCategories, activity.poiFilter);
			builder.setMultiChoiceItems(items, checkedItems, new OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					EventActivity activity = (EventActivity) getActivity();
					activity.poiFilter.put(activity.poiCategories[which], isChecked);
					activity.updatePois();
				}
			});

			builder.setPositiveButton("Hotovo", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});

			return builder.create();
		}

		private String[] extractNames(PoiCategory[] poiCategories) {
			String[] categories = new String[poiCategories.length];
			for (int i = 0; i < poiCategories.length; i++) {
				categories[i] = poiCategories[i].getName();
			}
			return categories;
		}

		private boolean[] extractChecked(PoiCategory[] poiCategories,
				Map<PoiCategory, Boolean> poiFilter) {
			boolean[] checked = new boolean[poiCategories.length];
			for (int i = 0; i < poiCategories.length; i++) {
				checked[i] = poiFilter.get(poiCategories[i]);
			}
			return checked;
		}
	}

	public static class IntentHelper {
		private static final String EXTRA_EVENT = "organization";

		public static Intent create(Context c, Event event) {
			Intent i = new Intent(c, EventActivity.class);
			i.putExtra(EXTRA_EVENT, event);
			return i;
		}

		public static Event getEvent(Intent i) {
			return (Event) i.getSerializableExtra(EXTRA_EVENT);
		}
	}
}
