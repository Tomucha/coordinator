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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.common.collect.Lists;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ConfigCall;
import cz.clovekvtisni.coordinator.android.api.EventPoiListCall;
import cz.clovekvtisni.coordinator.android.event.map.MapFragment;
import cz.clovekvtisni.coordinator.android.event.tasks.TasksFragment;
import cz.clovekvtisni.coordinator.android.util.SimpleListeners.SimpleTabListener;
import cz.clovekvtisni.coordinator.android.workers.BitmapLoader;
import cz.clovekvtisni.coordinator.android.workers.Workers;
import cz.clovekvtisni.coordinator.api.request.EventPoiListRequestParams;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
import cz.clovekvtisni.coordinator.api.response.EventPoiFilterResponseData;
import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.domain.config.PoiCategory;

public class EventActivity extends SherlockFragmentActivity implements
		LocationTool.BestLocationListener {

	private List<Fragment> fragments;
	private MapFragment mapFragment;
	private TasksFragment tasksFragment;
	private ViewPager pager;
	private Workers workers;

	private Map<PoiCategory, Boolean> poiFilter;
	private Map<PoiCategory, Bitmap> poiIcons = new HashMap<PoiCategory, Bitmap>();
	private Poi[] pois = new Poi[0];
	private PoiCategory[] poiCategories;

	private void initFragments() {
		mapFragment = new MapFragment();
		tasksFragment = new TasksFragment();

		fragments = Lists.newArrayList();
		fragments.add(mapFragment);
		fragments.add(tasksFragment);
		fragments.add(new TasksFragment());
		fragments.add(new TasksFragment());
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
	public void onBestLocationUpdated(Location location) {

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

		workers = new Workers(this);
		loadPoiCategories();
	}

	private void loadPois() {
		EventPoiListRequestParams params = new EventPoiListRequestParams();
		params.setEventId(IntentHelper.getEvent(getIntent()).getId());
		params.setModifiedFrom(new Date(0));
		workers.startOrConnect(new EventPoiListCall(params), new EventPoiListCall.Listener() {
			@Override
			public void onResult(EventPoiFilterResponseData result) {
				pois = result.getPois();
				updatePois();
			}

			@Override
			public void onException(Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void loadPoiIcons() {
		for (final PoiCategory category : poiCategories) {
			String url = category.getIcon();
			workers.startOrConnect(new BitmapLoader(url), new BitmapLoader.Listener() {
				@Override
				public void onSuccess(Bitmap bitmap) {
					int h = bitmap.getHeight() * 2;
					int w = bitmap.getWidth() * 2;
					bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
					poiIcons.put(category, bitmap);
					updatePois();
				}

				@Override
				public void onException(Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

	private void loadPoiCategories() {
		workers.startOrConnect(new ConfigCall(), new ConfigCall.Listener() {
			@Override
			public void onResult(ConfigResponse result) {
				poiCategories = result.getPoiCategoryList();
				onPoiCategoriesLoaded(poiCategories);
			}

			@Override
			public void onException(Exception e) {
			}
		});
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
