package cz.clovekvtisni.coordinator.android.registration;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.common.collect.Lists;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ApiCallAsyncLoader;
import cz.clovekvtisni.coordinator.android.api.ApiCallFactory;
import cz.clovekvtisni.coordinator.android.util.CommonTool;
import cz.clovekvtisni.coordinator.api.request.RegisterRequestParams;
import cz.clovekvtisni.coordinator.api.response.RegisterResponseData;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.config.Organization;

public class RegisterActivity extends SherlockFragmentActivity {

	private static final int LOADER_REGISTER = 0;

	private Button nextButton;
	private Button prevButton;

	private ArrayList<RegisterFragment> pages = Lists.newArrayList();
	private MyPagerAdapter pagerAdapter;
	private Organization organization;
	private User user;
	private ViewPager pager;

	private LoaderCallbacks<RegisterResponseData> registerCallbacks = new LoaderCallbacks<RegisterResponseData>() {
		@Override
		public Loader<RegisterResponseData> onCreateLoader(int id, Bundle args) {
			RegisterRequestParams params = new RegisterRequestParams();
			params.setNewUser(user);

			return new ApiCallAsyncLoader<RegisterRequestParams, RegisterResponseData>(
					getApplicationContext(), ApiCallFactory.register(), params);
		}

		@Override
		public void onLoadFinished(Loader<RegisterResponseData> loader, RegisterResponseData result) {
			ApiCallAsyncLoader apiLoader = (ApiCallAsyncLoader) loader;
			Exception e = apiLoader.getException();
			if (e != null) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				CommonTool.showToast(RegisterActivity.this, e.toString());
			} else {
			}
		}

		@Override
		public void onLoaderReset(Loader<RegisterResponseData> loader) {
		}
	};

	private void initBottomBar() {
		prevButton = (Button) findViewById(R.id.prev_button);
		nextButton = (Button) findViewById(R.id.next_button);

		prevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pager.setCurrentItem(pager.getCurrentItem() - 1);
			}
		});

		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pagerAdapter.primaryItem.updateUser(user);
				if (isLastPage()) register();
				else pager.setCurrentItem(pager.getCurrentItem() + 1);
			}
		});
	}

	private void initPager() {
		pages.add(new PersonalInfoFragment());
		pages.add(new AddressFragment());
		pages.add(EquipmentFragment.newInstance(organization.getPreRegistrationEquipment()));
		pages.add(SkillsFragment.newInstance(organization.getPreRegistrationSkills()));

		pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				updateBottomBar();
			}

		});
	}

	private boolean isLastPage() {
		return pager.getCurrentItem() == pages.size() - 1;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		organization = IntentHelper.getOrganization(getIntent());
		user = new User();
		user.setOrganizationId(organization.getId());

		initPager();
		initBottomBar();
		updateBottomBar();
	}

	private void register() {
		getSupportLoaderManager().initLoader(LOADER_REGISTER, null, registerCallbacks);
		new RegisteringDialog().show(getSupportFragmentManager(), null);
	}

	private void updateBottomBar() {
		int position = pager.getCurrentItem();
		prevButton.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);

		boolean isLastPage = position == pages.size() - 1;
		nextButton.setText(isLastPage ? "Dokončit" : "Další");
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {
		private RegisterFragment primaryItem;
		
		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			return pages.get(i);
		}

		@Override
		public int getCount() {
			return pages.size();
		}
		
		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			super.setPrimaryItem(container, position, object);
			primaryItem = (RegisterFragment) object;
		}
	}

	public static class RegisteringDialog extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle state) {
			final ProgressDialog dialog = new ProgressDialog(getActivity());
			dialog.setMessage("Probíhá registrace!");
			dialog.setCancelable(false);
			return dialog;
		}
	}

	public static class IntentHelper {
		private static final String EXTRA_ORGANIZATION = "organization";

		public static Intent create(Context c, Organization o) {
			Intent i = new Intent(c, RegisterActivity.class);
			i.putExtra(EXTRA_ORGANIZATION, o);
			return i;
		}

		public static Organization getOrganization(Intent i) {
			return (Organization) i.getSerializableExtra(EXTRA_ORGANIZATION);
		}
	}

}
