package cz.clovekvtisni.coordinator.android.event;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.common.collect.Lists;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.EventPoiListCall;
import cz.clovekvtisni.coordinator.android.event.map.MapFragment;
import cz.clovekvtisni.coordinator.android.event.tasks.TasksFragment;
import cz.clovekvtisni.coordinator.android.organization.OrganizationActivity;
import cz.clovekvtisni.coordinator.android.util.SimpleListeners.SimpleTabListener;
import cz.clovekvtisni.coordinator.android.workers.Workers;
import cz.clovekvtisni.coordinator.api.request.EventPoiListRequestParams;
import cz.clovekvtisni.coordinator.api.response.EventPoiFilterResponseData;
import cz.clovekvtisni.coordinator.domain.config.Organization;

public class EventActivity extends SherlockFragmentActivity implements LocationTool.BestLocationListener {

	private List<Fragment> fragments;
	private ViewPager pager;

	private void initFragments() {
		fragments = Lists.newArrayList();
		fragments.add(new MapFragment());
		fragments.add(new TasksFragment());
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
		
		Workers workers = new Workers(this);
		EventPoiListRequestParams params = new EventPoiListRequestParams();
		params.setEventId(1L);
		params.setModifiedFrom(new Date(0));
		workers.startOrConnect(new EventPoiListCall(params), new EventPoiListCall.Listener() {
			@Override
			public void onResult(EventPoiFilterResponseData result) {
				System.out.println(result.getPois().length);
			}
			
			@Override
			public void onException(Exception e) {
				e.printStackTrace();
			}
		});
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

	public static class IntentHelper {
		public static Intent create(Context c) {
			Intent i = new Intent(c, EventActivity.class);
			return i;
		}
	}

}
