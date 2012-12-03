package cz.clovekvtisni.coordinator.android.register;

import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ApiCallAsyncLoader;
import cz.clovekvtisni.coordinator.android.api.ApiCallFactory;
import cz.clovekvtisni.coordinator.android.register.wizard.model.ModelCallbacks;
import cz.clovekvtisni.coordinator.android.register.wizard.model.Page;
import cz.clovekvtisni.coordinator.android.register.wizard.model.WizardModel;
import cz.clovekvtisni.coordinator.android.register.wizard.ui.PageFragmentCallbacks;
import cz.clovekvtisni.coordinator.android.register.wizard.ui.ReviewFragment;
import cz.clovekvtisni.coordinator.android.util.CommonTool;
import cz.clovekvtisni.coordinator.api.request.RegisterRequestParams;
import cz.clovekvtisni.coordinator.api.response.RegisterResponseData;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.config.Organization;

public class RegisterActivity extends FragmentActivity implements PageFragmentCallbacks,
		ReviewFragment.Callbacks, ModelCallbacks {
	private static final int LOADER_REGISTER = 0;

	private ViewPager mPager;
	private MyPagerAdapter mPagerAdapter;

	private boolean mEditingAfterReview;

	private WizardModel mWizardModel;

	private boolean mConsumePageSelectedEvent;

	private Button mNextButton;
	private Button mPrevButton;

	private List<Page> mCurrentPageSequence;

	private Organization organization;

	private void initWizardList(Bundle state) {
		mWizardModel = new WizardModel(this, organization);
		if (state != null) mWizardModel.load(state.getBundle("model"));
		mWizardModel.registerListener(this);
	}

	public void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_register);

		organization = IntentHelper.getOrganization(getIntent());

		initWizardList(state);

		mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mPagerAdapter);

		mNextButton = (Button) findViewById(R.id.next_button);
		mPrevButton = (Button) findViewById(R.id.prev_button);

		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (mConsumePageSelectedEvent) {
					mConsumePageSelectedEvent = false;
					return;
				}

				mEditingAfterReview = false;
				updateBottomBar();
			}
		});

		mNextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
					register();
				} else {
					if (mEditingAfterReview) {
						mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
					} else {
						mPager.setCurrentItem(mPager.getCurrentItem() + 1);
					}
				}
			}
		});

		mPrevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mPager.setCurrentItem(mPager.getCurrentItem() - 1);
			}
		});

		onPageTreeChanged();
		updateBottomBar();
	}

	@Override
	public void onPageTreeChanged() {
		mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
		recalculateCutOffPage();
		mPagerAdapter.notifyDataSetChanged();
		updateBottomBar();
	}

	private void updateBottomBar() {
		int position = mPager.getCurrentItem();
		if (position == mCurrentPageSequence.size()) {
			mNextButton.setText(R.string.finish);
			mNextButton.setBackgroundResource(R.drawable.btn_register);
			mNextButton.setTextColor(0xffffffff);
		} else {
			mNextButton.setText(mEditingAfterReview ? R.string.review : R.string.next);
			mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
			TypedValue v = new TypedValue();
			getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
			mNextButton.setTextAppearance(this, v.resourceId);
			mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
		}

		mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWizardModel.unregisterListener(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBundle("model", mWizardModel.save());
	}

	@Override
	public WizardModel onGetModel() {
		return mWizardModel;
	}

	@Override
	public void onEditScreenAfterReview(String key) {
		for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
			if (mCurrentPageSequence.get(i).getKey().equals(key)) {
				mConsumePageSelectedEvent = true;
				mEditingAfterReview = true;
				mPager.setCurrentItem(i);
				updateBottomBar();
				break;
			}
		}
	}

	@Override
	public void onPageDataChanged(Page page) {
		if (page.isRequired()) {
			if (recalculateCutOffPage()) {
				mPagerAdapter.notifyDataSetChanged();
				updateBottomBar();
			}
		}
	}

	@Override
	public Page onGetPage(String key) {
		return mWizardModel.findByKey(key);
	}

	private boolean recalculateCutOffPage() {
		// Cut off the pager adapter at first required page that isn't completed
		int cutOffPage = mCurrentPageSequence.size() + 1;
		for (int i = 0; i < mCurrentPageSequence.size(); i++) {
			Page page = mCurrentPageSequence.get(i);
			if (page.isRequired() && !page.isCompleted()) {
				cutOffPage = i;
				break;
			}
		}

		if (mPagerAdapter.getCutOffPage() != cutOffPage) {
			mPagerAdapter.setCutOffPage(cutOffPage);
			return true;
		}

		return false;
	}

	private void register() {
		User user = new User();
		user.setOrganizationId(organization.getId());
		mWizardModel.saveToUser(user);

		Bundle b = new Bundle();
		b.putSerializable("user", user);
		getSupportLoaderManager().initLoader(LOADER_REGISTER, b, registerCallbacks);

		new RegisteringDialog().show(getSupportFragmentManager(), null);
	}

	private LoaderCallbacks<RegisterResponseData> registerCallbacks = new LoaderCallbacks<RegisterResponseData>() {
		@Override
		public Loader<RegisterResponseData> onCreateLoader(int id, Bundle args) {
			RegisterRequestParams params = new RegisterRequestParams();
			params.setNewUser((User) args.getSerializable("user"));

			return new ApiCallAsyncLoader<RegisterRequestParams, RegisterResponseData>(
					getApplicationContext(), ApiCallFactory.register(), params);
		}

		@Override
		public void onLoadFinished(Loader<RegisterResponseData> loader, RegisterResponseData result) {
			ApiCallAsyncLoader apiLoader = (ApiCallAsyncLoader) loader;
			Exception e = apiLoader.getException();
			if (e != null) {
				CommonTool.showToast(RegisterActivity.this, e.toString());
			} else {
				finish();
			}
		}

		@Override
		public void onLoaderReset(Loader<RegisterResponseData> loader) {
		}
	};

	public class MyPagerAdapter extends FragmentStatePagerAdapter {
		private int mCutOffPage;
		private Fragment mPrimaryItem;

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			if (i >= mCurrentPageSequence.size()) {
				return new ReviewFragment();
			}

			System.out.println(mCurrentPageSequence.get(i).getClass());
			return mCurrentPageSequence.get(i).createFragment();
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO: be smarter about this
			if (object == mPrimaryItem) {
				// Re-use the current fragment (its position never changes)
				return POSITION_UNCHANGED;
			}

			return POSITION_NONE;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			super.setPrimaryItem(container, position, object);
			mPrimaryItem = (Fragment) object;
		}

		@Override
		public int getCount() {
			return Math.min(mCutOffPage + 1, mCurrentPageSequence.size() + 1);
		}

		public void setCutOffPage(int cutOffPage) {
			if (cutOffPage < 0) {
				cutOffPage = Integer.MAX_VALUE;
			}
			mCutOffPage = cutOffPage;
		}

		public int getCutOffPage() {
			return mCutOffPage;
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
