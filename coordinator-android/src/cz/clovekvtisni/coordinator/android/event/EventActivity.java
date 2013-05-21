package cz.clovekvtisni.coordinator.android.event;

import java.io.IOException;
import java.util.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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
import cz.clovekvtisni.coordinator.android.api.ApiLoader;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.ConfigLoader;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.ConfigLoaderListener;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.EventPoiListLoader;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.EventPoiListLoaderListener;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.EventUserListLoader;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.EventUserListLoaderListener;
import cz.clovekvtisni.coordinator.android.api.BitmapLoader;
import cz.clovekvtisni.coordinator.android.event.map.view.NetworkTileLoader;
import cz.clovekvtisni.coordinator.android.event.map.view.NetworkTileLoader.RemainingTilesListener;
import cz.clovekvtisni.coordinator.android.event.map.view.TileCache;
import cz.clovekvtisni.coordinator.android.other.Settings;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.android.util.SimpleListeners.SimpleTabListener;
import cz.clovekvtisni.coordinator.android.util.UiTool;
import cz.clovekvtisni.coordinator.android.util.Utils;
import cz.clovekvtisni.coordinator.api.request.EventPoiListRequestParams;
import cz.clovekvtisni.coordinator.api.request.EventUserListRequestParams;
import cz.clovekvtisni.coordinator.api.request.UserUpdatePositionRequestParams;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
import cz.clovekvtisni.coordinator.api.response.EventPoiFilterResponseData;
import cz.clovekvtisni.coordinator.api.response.EventUserListResponseData;
import cz.clovekvtisni.coordinator.api.response.UserUpdatePositionResponseData;
import cz.clovekvtisni.coordinator.domain.*;
import cz.clovekvtisni.coordinator.domain.config.PoiCategory;

public class EventActivity extends SherlockFragmentActivity implements LocationTool.Listener {

	private Event event;
	private List<SherlockFragment> fragments;
	private LocationTool locationTool;
	private InfoFragment infoFragment;
	private MapFragment mapFragment;
	private NetworkTileLoader netTileLoader;
	private TasksFragment tasksFragment;
	private UsersFragment usersFragment;
	private ViewPager pager;

	private Map<PoiCategory, Boolean> poiFilter;
    private Map<UserGroup, Boolean> userFilter;

	private Map<PoiCategory, Bitmap> poiIcons = new HashMap<PoiCategory, Bitmap>();
	private Poi[] pois = new Poi[0];
	private PoiCategory[] poiCategories;
    private Long zoomToPoi;
    private List<UserInEvent> usersList;

    private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setLogo(new ColorDrawable(Color.TRANSPARENT));
	}

	private void initFragments() {
		mapFragment = new MapFragment();

        if (zoomToPoi != null) {
            mapFragment.zoomToPoi(zoomToPoi);
        }

        tasksFragment = new TasksFragment();
		usersFragment = new UsersFragment();
		String info = IntentHelper.getOrganizationInEvent(getIntent()).getOperationalInfo();
		infoFragment = InfoFragment.newInstance(info);

		fragments = Lists.newArrayList(mapFragment, tasksFragment, usersFragment, infoFragment);
	}

	private void initMapPreload() {
		if (!Settings.isEventMapPreloaded(event.getId())) {
			new AskIfPreloadDialog().show(getSupportFragmentManager(), AskIfPreloadDialog.TAG);
		}
	}

	private void initNetTileLoader() {
		TileCache cache;
		try {
			cache = TileCache.getInstance();
		} catch (IOException e) {
			e.printStackTrace();
			throw new AssertionError();
		}

		netTileLoader = new NetworkTileLoader(cache, new Handler());
		mapFragment.setNetTileLoader(netTileLoader);
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
        Lg.APP.i("Starting EventActivity");
		setContentView(R.layout.activity_event);
        UiTool.dropNotification(this);

		event = IntentHelper.getEvent(getIntent());

        zoomToPoi = EventActivity.IntentHelper.getPOI(getIntent());
        if (zoomToPoi != null) {
            Lg.APP.i("Will zoom to "+zoomToPoi);
            IntentHelper.removePOI(getIntent());
        }

        initActionBar();
        initFragments();
		initPager();
		initTabs();

		locationTool = new LocationTool(this, this);
	}

	public void onMapFragmentReady() {
		initNetTileLoader();
		initMapPreload();
		loadUsers();
		loadPoiCategories();
	}

    @Override
    public void onStart() {
        super.onStart();

    }

	@Override
	public void onResume() {
		super.onResume();
        /*
        // if we do this in onCreate, it might end up with NPE in
        // MapFragment.setFilteredUsers
        loadUsers();
        loadPoiCategories();
        */
        locationTool.resume();

	}

	@Override
	public void onPause() {
		super.onPause();
        mapFragment.saveMapSettings();
		locationTool.pause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.event, menu);
		return true;
	}

	public void loadPois(boolean reload) {
        Lg.APP.i("Loading POIs");
		EventPoiListRequestParams params = new EventPoiListRequestParams();
		params.setEventId(event.getId());
		params.setModifiedFrom(new Date(0));
		EventPoiListLoader loader = (EventPoiListLoader) Workers.load(new EventPoiListLoader(params), new EventPoiListLoaderListener() {
            @Override
            public void onResult(EventPoiFilterResponseData result) {
                pois = result.getPois();
                updateImportantPois();
                updateFilteredPois();
            }

            @Override
            public void onInternetException(Exception e) {
                UiTool.toast(R.string.error_no_internet, getApplicationContext());
            }
        }, this);
        if (zoomToPoi != null || reload) {
            // FIXME: pokud byl loader prave vytvoren, pusti se load 2x
            Lg.APP.i("Reload required POIs");
            loader.reload();
        }
	}

	private void loadPoiIcons() {
		for (final PoiCategory category : poiCategories) {
			String url = category.getIcon();
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			Workers.load(new BitmapLoader(url), new BitmapLoader.Listener() {
				@Override
				public void onSuccess(Bitmap bitmap) {
					bitmap = Utils.scaleBitmapAccordingToDensity(bitmap, getWindowManager());
					poiIcons.put(category, bitmap);
					updateFilteredPois();
					updateImportantPois();
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
			public void onInternetException(Exception e) {
                UiTool.toast(R.string.error_no_internet, getApplicationContext());
                finish();
			}
		}, this);
	}

	private void loadUsers() {
		EventUserListRequestParams params = new EventUserListRequestParams();
		params.setEventId(event.getId());
		params.setModifiedFrom(new Date(0));
		Workers.load(new EventUserListLoader(params), new EventUserListLoaderListener() {
			@Override
			public void onResult(EventUserListResponseData result) {
                userFilter = new HashMap<UserGroup, Boolean>();
                usersList = Lists.newArrayList(result.getUserInEvents());
                updateFilteredUsers();
			}

			@Override
			public void onInternetException(Exception e) {
                UiTool.toast(R.string.error_no_internet, getApplicationContext());
                finish();
			}
		}, this);
	}

	public void onPoiCategoriesLoaded(PoiCategory[] poiCategories) {
		this.poiCategories = poiCategories;
		this.poiFilter = new HashMap<PoiCategory, Boolean>();

		for (PoiCategory category : poiCategories) {
			poiFilter.put(category, true);
		}

		loadPois(false);
		loadPoiIcons();
	}

	private void startMapPreload() {
		final PreloadingDialog dialog = new PreloadingDialog();
		dialog.show(getSupportFragmentManager(), PreloadingDialog.TAG);

		netTileLoader.setRemainingTilesListener(new RemainingTilesListener() {
			@Override
			public void onRemainingTilesChanged(int remainingTiles) {
				if (dialog.getDialog() == null) return;
				dialog.setRemainingTiles(remainingTiles);
				if (remainingTiles == 0) {
					dialog.dismiss();
					Settings.setEventMapPreloaded(event.getId());
					netTileLoader.removeRemainingTilesListener(this);
				}
			}
		});
		netTileLoader.preloadTiles(event.getLocationList());
	}

    private void updateFilteredUsers() {
        List<UserInEvent> filteredUsers = new ArrayList<UserInEvent>();
        if (userFilter.isEmpty()) {
            mapFragment.setFilteredUsers(usersList);
            usersFragment.setFilteredUsers(usersList);
            return;
        }

        for (UserInEvent user : usersList) {
            List<UserGroup> groups = user.getGroups();
            if (groups != null) {
                for (UserGroup group : groups) {
                    if (userFilter.get(group)!=null && userFilter.get(group)) {
                        filteredUsers.add(user);
                        break;
                    }
                }
            }
        }

        mapFragment.setFilteredUsers(filteredUsers);
        usersFragment.setFilteredUsers(filteredUsers);
    }


	private void updateFilteredPois() {
		List<Poi> filteredPois = new ArrayList<Poi>();
		for (Poi poi : pois) {
			if (poiFilter.get(poi.getPoiCategory())) filteredPois.add(poi);
		}

		mapFragment.setFilteredPois(filteredPois, poiIcons);
		tasksFragment.setFilteredPois(filteredPois);
	}

	private void updateImportantPois() {
		List<Poi> importantPois = new ArrayList<Poi>();
		for (Poi poi : pois) {
			if (poi.getPoiCategory().isImportant()) importantPois.add(poi);
		}

		infoFragment.setImportantPois(importantPois);
	}

	public void showPoiFilterDialog() {
		new PoiFilterDialog().show(getSupportFragmentManager(), PoiFilterDialog.TAG);
	}

    public void showPeopleFilterDialog() {
        new UserFilterDialog().show(getSupportFragmentManager(), UserFilterDialog.TAG);
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
	public void onDestroy() {
		super.onDestroy();
		netTileLoader.shutDown();
	}

	@Override
	public void onLocationShouldBeUploaded(Location location) {
		Lg.LOCATION.d("Location should be uploaded. " + describeLocation(location));
		Workers.start(new UploadMyLocationTask(location, event.getId()), this);
	}

	private String describeLocation(Location location) {
		return "Accuracy: " + location.getAccuracy() + " m";
	}

    public long getEventId() {
        return event.getId();
    }

    public Event getEvent() {
        return event;
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

	public static class AskIfPreloadDialog extends SherlockDialogFragment {
		private static final String TAG = "ask-if-preload-dialog";

		@Override
		public Dialog onCreateDialog(Bundle state) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(getString(R.string.preload_question));
			builder.setMessage(getString(R.string.preload_info));
			builder.setNegativeButton(getString(R.string.preload_button_no), null);
			builder.setPositiveButton(getString(R.string.preload_button_yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					((EventActivity) getActivity()).startMapPreload();
				}
			});
			return builder.create();
		}
	}

	public static class PreloadingDialog extends SherlockDialogFragment {
		private static final String TAG = "preloading-dialog";

		@Override
		public Dialog onCreateDialog(Bundle state) {
			ProgressDialog dialog = new ProgressDialog(getActivity());
			dialog.setMessage(getString(R.string.preload_downloading));
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			return dialog;
		}

		public void setRemainingTiles(int remaining) {
			ProgressDialog d = (ProgressDialog) getDialog();
			d.setMessage("Zbývá " + remaining + " dlaždic.");
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
					activity.updateFilteredPois();
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


    public static class UserFilterDialog extends SherlockDialogFragment {

        private static final String TAG = "user-filter-dialog";

        @Override
        public Dialog onCreateDialog(Bundle state) {
            EventActivity activity = (EventActivity) getActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(activity.getString(R.string.dialog_title_filter_people));

            final UserGroup items[] = extractGroups(activity.usersList);
            String[] names = new String[items.length];

            if (items == null || items.length == 0) {
                UiTool.toast(R.string.warning_no_groups_to_filter, getActivity().getApplicationContext());
            }


            for (int i = 0; i < items.length; i++) {
                UserGroup item = items[i];
                names[i] = item.getName();
            }

            boolean[] checkedItems = extractChecked(items, activity.userFilter);

            builder.setMultiChoiceItems(names, checkedItems, new OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    EventActivity activity = (EventActivity) getActivity();
                    if (isChecked) {
                        activity.userFilter.put(items[which], true);
                    } else {
                        activity.userFilter.remove(items[which]);
                    }
                    activity.updateFilteredUsers();
                }
            });

            builder.setPositiveButton("Hotovo", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            return builder.create();
        }

        private UserGroup[] extractGroups(List<UserInEvent> users) {
            Set<UserGroup> groups = new HashSet<UserGroup>();

            for (UserInEvent user : users) {
                Lg.APP.i("User groups: "+user+"  "+user.getGroups());
                if (user.getGroups() != null && user.getGroups() != null) {
                    groups.addAll(user.getGroups());
                }
            }

            UserGroup[] result = groups.toArray(new UserGroup[0]);

            Arrays.sort(result, new Comparator<UserGroup>() {
                @Override
                public int compare(UserGroup userGroup, UserGroup userGroup2) {
                    return userGroup.getName().compareTo(userGroup2.getName());
                }
            });

            return result;
        }

        private boolean[] extractChecked(UserGroup[] userGroups, Map<UserGroup, Boolean> userFilter) {
            boolean[] checked = new boolean[userGroups.length];
            for (int i = 0; i < userGroups.length; i++) {
                checked[i] = userFilter.get(userGroups[i]) != null && userFilter.get(userGroups[i]);
            }
            return checked;
        }
    }

	public static class IntentHelper {
		private static final String EXTRA_EVENT = "event";
        private static final String EXTRA_POI = "poiId";
		private static final String EXTRA_ORG_IN_EVENT = "orgInEvent";

        public static Intent create(Context c, Event event, OrganizationInEvent organizationInEvent, long poiId) {
            Intent i = create(c, event, organizationInEvent);
            i.putExtra(EXTRA_POI, poiId);
            return i;
        }

		public static Intent create(Context c, Event event, OrganizationInEvent organizationInEvent) {
			Intent i = new Intent(c, EventActivity.class);
			i.putExtra(EXTRA_EVENT, event);
			i.putExtra(EXTRA_ORG_IN_EVENT, organizationInEvent);
			return i;
		}

		public static Event getEvent(Intent i) {
			return (Event) i.getSerializableExtra(EXTRA_EVENT);
		}

		public static OrganizationInEvent getOrganizationInEvent(Intent i) {
			return (OrganizationInEvent) i.getSerializableExtra(EXTRA_ORG_IN_EVENT);
		}

        public static Long getPOI(Intent i) {
            if (i.hasExtra(EXTRA_POI)) {
                return i.getLongExtra(EXTRA_POI, 0);
            }
            return null;
        }

        public static void removePOI(Intent i ) {
            if (i.hasExtra(EXTRA_POI)) {
                i.removeExtra(EXTRA_POI);
            }
        }
    }

    public PoiCategory[] getPoiCategories() {
        // TODO: unmodifieable
        return poiCategories;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("DO NOT CRASH", "OK");
        super.onSaveInstanceState(outState);
    }
}
