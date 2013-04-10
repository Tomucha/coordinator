package cz.clovekvtisni.coordinator.android.register;

import java.io.Serializable;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.fhucho.android.workers.Workers;
import com.fhucho.android.workers.simple.ActivityWorker2;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ApiCall.ApiCallException;
import cz.clovekvtisni.coordinator.android.api.ApiCalls.UserPushTokenCall;
import cz.clovekvtisni.coordinator.android.api.ApiCalls.UserRegisterCall;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.ConfigLoader;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.ConfigLoaderListener;
import cz.clovekvtisni.coordinator.android.other.Settings;
import cz.clovekvtisni.coordinator.android.register.wizard.model.ModelCallbacks;
import cz.clovekvtisni.coordinator.android.register.wizard.model.Page;
import cz.clovekvtisni.coordinator.android.register.wizard.model.WizardModel;
import cz.clovekvtisni.coordinator.android.register.wizard.ui.PageFragmentCallbacks;
import cz.clovekvtisni.coordinator.android.register.wizard.ui.ReviewFragment;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.android.util.UiTool;
import cz.clovekvtisni.coordinator.api.request.RegisterRequestParams;
import cz.clovekvtisni.coordinator.api.request.UserPushTokenRequestParams;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
import cz.clovekvtisni.coordinator.api.response.RegisterResponseData;
import cz.clovekvtisni.coordinator.domain.Event;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.UserInEvent;
import cz.clovekvtisni.coordinator.domain.config.Organization;

public class RegisterActivity extends SherlockFragmentActivity implements PageFragmentCallbacks,
		ReviewFragment.Callbacks, ModelCallbacks {
	private ViewPager mPager;
	private MyPagerAdapter mPagerAdapter;

	private boolean mEditingAfterReview;

	private WizardModel mWizardModel;

	private boolean mConsumePageSelectedEvent;

	private Button nextButton;
	private Button prevButton;

	private List<Page> mCurrentPageSequence;

	private Event event;
	private Organization organization;

	private ConfigResponse config;
    private User user;

    private void initWizardModel(Bundle state) {
		mWizardModel = new WizardModel(this, organization, config, user);
		if (state != null) mWizardModel.load(state.getBundle("model"));
		mWizardModel.registerListener(this);
		mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
	}

	private void initPager() {
		mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mPagerAdapter);

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
	}

	private void initBottomBar() {
		nextButton = (Button) findViewById(R.id.next_button);
		prevButton = (Button) findViewById(R.id.prev_button);

		nextButton.setOnClickListener(new View.OnClickListener() {
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

		prevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mPager.setCurrentItem(mPager.getCurrentItem() - 1);
			}
		});
	}

	public void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_register);

		Intent intent = getIntent();
		organization = IntentHelper.getOrganization(intent);
		event = IntentHelper.getEvent(intent);
        user = IntentHelper.getUser(intent);

		getSupportActionBar().setTitle(event == null ? "Předregistrace" : event.getName());

		if (state != null && state.containsKey("config")) {
			config = ((ConfigSerializableWrapper) state.getSerializable("config")).config;
			initWizard(state);
		} else {
			loadConfig();
		}
	}

	private void initWizard(Bundle state) {
		initWizardModel(state);
		initBottomBar();
		initPager();
		onPageTreeChanged();
		updateBottomBar();
	}

	private void loadConfig() {
		Workers.load(new ConfigLoader(), new ConfigLoaderListener() {
			@Override
			public void onResult(ConfigResponse config) {
				RegisterActivity.this.config = config;
				initWizard(null);
			}

			@Override
			public void onInternetException(Exception e) {
                Lg.API.e("Cannot load config: "+e, e);
                UiTool.toast(R.string.error_no_internet, getApplicationContext());
			}
		}, this);
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
			nextButton.setText(R.string.finish);
			nextButton.setBackgroundResource(R.drawable.btn_register);
			nextButton.setTextColor(0xffffffff);
		} else {
			nextButton.setText(mEditingAfterReview ? R.string.review : R.string.next);
			nextButton.setBackgroundResource(R.drawable.selectable_item_background);
			TypedValue v = new TypedValue();
			getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
			nextButton.setTextAppearance(this, v.resourceId);
			nextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
		}

		prevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mWizardModel != null) mWizardModel.unregisterListener(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBundle("model", mWizardModel.save());
		if (config != null) {
			outState.putSerializable("config", new ConfigSerializableWrapper(config));
		}
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

		RegisterRequestParams params = new RegisterRequestParams();
		params.setNewUser(user);

		Event event = IntentHelper.getEvent(getIntent());
		if (event != null) {
			UserInEvent userInEvent = new UserInEvent();
			userInEvent.setEventId(event.getId());
			params.setUserInEvent(userInEvent);
		}

		Workers.start(new UserRegisterTask(params), this);

		new RegisteringDialog().show(getSupportFragmentManager(), RegisteringDialog.TAG);
	}

	private static class UserRegisterTask extends
			ActivityWorker2<RegisterActivity, RegisterResponseData, Exception> {

		private final RegisterRequestParams params;

		public UserRegisterTask(RegisterRequestParams params) {
			this.params = params;
		}

		@Override
		protected void doInBackground() {
			try {
				RegisterResponseData result = new UserRegisterCall(params).call();

                if (result.getAuthKey() != null) {
                    // auth key was changed
				    Settings.setAuthKey(result.getAuthKey());
                }
				
				try {
					String regId = Settings.getGcmRegistrationId();
					UserPushTokenRequestParams params = new UserPushTokenRequestParams(regId);
					new UserPushTokenCall(params).call();
					Lg.GCM.d("Successfully uploaded registration id to the server.");
				} catch (ApiCallException e) {
					Lg.GCM.w("Uploading registration id to the server unsuccessful.", e);
				}
				
				getListenerProxy().onSuccess(result);
			} catch (ApiCallException e) {
				getListenerProxy().onException(e);
			}
		}

		@Override
		public void onSuccess(RegisterResponseData result) {
			getActivity().finish();
		}

		@Override
		public void onException(Exception e) {
			Toast.makeText(getActivity(), "Chyba", Toast.LENGTH_SHORT).show();
			FragmentManager fm = getActivity().getSupportFragmentManager();
			((DialogFragment) fm.findFragmentByTag(RegisteringDialog.TAG)).dismiss();
		}

	}

	private class MyPagerAdapter extends FragmentStatePagerAdapter {
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

			return mCurrentPageSequence.get(i).createFragment();
		}

		@Override
		public int getItemPosition(Object object) {
			if (object == mPrimaryItem) {
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

	@SuppressWarnings("serial")
	private static class ConfigSerializableWrapper implements Serializable {
		private ConfigResponse config;

		public ConfigSerializableWrapper(ConfigResponse config) {
			this.config = config;
		}
	}

	public static class RegisteringDialog extends SherlockDialogFragment {
		private static final String TAG = "registering-dialog";

		@Override
		public Dialog onCreateDialog(Bundle state) {
			final ProgressDialog dialog = new ProgressDialog(getActivity());
			dialog.setMessage("Probíhá registrace!");
			dialog.setCancelable(false);
			return dialog;
		}
	}

	public static class IntentHelper {
		private static final String EXTRA_EVENT = "event";
		private static final String EXTRA_ORGANIZATION = "organization";
        private static final String EXTRA_USER = "user";

        public static Intent create(Context c, Organization o, Event e, User user) {
			Intent i = new Intent(c, RegisterActivity.class);
			i.putExtra(EXTRA_ORGANIZATION, o);
			i.putExtra(EXTRA_EVENT, e);
            i.putExtra(EXTRA_USER, user);
			return i;
		}

		public static Event getEvent(Intent i) {
			return (Event) i.getSerializableExtra(EXTRA_EVENT);
		}

        public static User getUser(Intent i) {
            return (User) i.getSerializableExtra(EXTRA_USER);
        }

		public static Organization getOrganization(Intent i) {
			return (Organization) i.getSerializableExtra(EXTRA_ORGANIZATION);
		}
	}
}
