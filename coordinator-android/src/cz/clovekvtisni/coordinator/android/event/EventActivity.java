package cz.clovekvtisni.coordinator.android.event;

import java.util.List;

import android.content.Context;
import android.content.Intent;
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
import cz.clovekvtisni.coordinator.android.event.map.MapFragment;
import cz.clovekvtisni.coordinator.android.event.tasks.TasksFragment;
import cz.clovekvtisni.coordinator.android.ui.OrganizationActivity;
import cz.clovekvtisni.coordinator.android.util.SimpleListeners.SimpleTabListener;
import cz.clovekvtisni.coordinator.android.workers.Workers;
import cz.clovekvtisni.coordinator.domain.config.Organization;

public class EventActivity extends SherlockFragmentActivity {

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
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_organization_home);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		initFragments();
		initPager();
		initTabs();
		
		Workers workers = new Workers(this);
		//workers.startOrConnect(new, listener)
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
		private static final String EXTRA_ORGANIZATION = "organization";

		public static Intent create(Context c, Organization o) {
			Intent i = new Intent(c, OrganizationActivity.class);
			i.putExtra(EXTRA_ORGANIZATION, o);
			return i;
		}

		public static Organization getOrganization(Intent i) {
			return (Organization) i.getSerializableExtra(EXTRA_ORGANIZATION);
		}
	}

}
