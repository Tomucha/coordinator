package cz.clovekvtisni.coordinator.android.welcome;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.fhucho.android.workers.Workers;
import com.fhucho.android.workers.simple.ActivityWorker2;
import com.google.android.gcm.GCMRegistrar;
import cz.clovekvtisni.coordinator.SecretInfo;
import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.api.ApiCall;
import cz.clovekvtisni.coordinator.android.api.ApiCalls;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.ConfigLoader;
import cz.clovekvtisni.coordinator.android.api.ApiLoaders.ConfigLoaderListener;
import cz.clovekvtisni.coordinator.android.api.BitmapLoader;
import cz.clovekvtisni.coordinator.android.organization.OrganizationActivity;
import cz.clovekvtisni.coordinator.android.other.Settings;
import cz.clovekvtisni.coordinator.android.util.BetterArrayAdapter;
import cz.clovekvtisni.coordinator.android.util.FindView;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.android.util.UiTool;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
import cz.clovekvtisni.coordinator.api.response.UserByIdResponseData;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.config.Organization;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends SherlockFragmentActivity {

    private final Map<Organization, Bitmap> organizationIcons = new HashMap<Organization, Bitmap>();
    private OrganizationAdapter adapter;
    private boolean logged;
    private Organization[] organizations = null;
    private User myself;

    private void initGCM() {
        Lg.GCM.i("GCM init");
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            Lg.GCM.i("Going to register.");
            GCMRegistrar.register(this, SecretInfo.GCM_SENDER_ID);
        } else {
            Lg.GCM.i("Already registered to GCM");
        }
    }

    private void initListView() {
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onOrganizationSelected(adapter.getItem(position));
            }
        });
    }

    private void loadOrganizations() {
        Workers.load(new ConfigLoader(), new ConfigLoaderListener() {
            @Override
            public void onResult(ConfigResponse result) {
                onOrganizationsLoaded(result.getOrganizationList());
            }

            @Override
            public void onInternetException(Exception e) {
                UiTool.toast(R.string.error_no_internet, getApplicationContext());
            }
        }, this);
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);

        Lg.APP.i("Starting with "+Settings.getAuthKey());

        logged = Settings.getAuthKey() != null;

        adapter = new OrganizationAdapter();

        loadOrganizations();

        if (logged) {
            ((TextView)findViewById(R.id.app_welcome_description)).setText(R.string.app_welcome_description_wait);
            loadMyself();
        }
        initListView();
        initGCM();
    }

    private void loadMyself() {
        Workers.start(new UserInfoTask(), this);
    }


    private class UserInfoTask extends ActivityWorker2<MainActivity, UserByIdResponseData, Exception> {

        public UserInfoTask() {
        }

        @Override
        protected void doInBackground() {
            try {
                UserByIdResponseData result = new ApiCalls.UserInfoCall().call();
                getListenerProxy().onSuccess(result);
            } catch (ApiCall.ApiCallException e) {
                getListenerProxy().onException(e);
            }
        }

        @Override
        public void onSuccess(UserByIdResponseData result) {
            onUserLoaded(result.getFirst());
        }

        @Override
        public void onException(Exception e) {
            Lg.API.e("Cannot load myself: "+e, e);
            UiTool.toast(R.string.error_no_internet, getActivity().getApplicationContext());
        }

    }

    protected void onUserLoaded(User first) {
        myself = first;
        gotoMyOrganization();
    }

    private void gotoMyOrganization() {
        if (!isFinishing()) {
            if (myself != null && organizations != null) {
                // we are registered
                for (Organization organization : organizations) {
                    if (myself.getOrganizationId().equals(organization.getId())) {
                        onOrganizationSelected(organization);
                        finish();
                        return;
                    }
                }
            }
        }
    }


    private void onOrganizationsLoaded(Organization[] organizations) {
        adapter.clear();

        this.organizations = organizations;

        if (logged) {
            gotoMyOrganization();

        } else {
            adapter.addAll(organizations);
            adapter.notifyDataSetChanged();

            for (final Organization organization : organizations) {
                Workers.load(new BitmapLoader(organization.getIcon()), new BitmapLoader.Listener() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        organizationIcons.put(organization, bitmap);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onException(Exception e) {
                        throw new IllegalStateException(e);
                    }
                }, this);
            }
        }
    }

    private void onOrganizationSelected(Organization organization) {
        startActivity(OrganizationActivity.IntentHelper.create(this, organization, myself));
    }

    private class OrganizationAdapter extends BetterArrayAdapter<Organization> {
        public OrganizationAdapter() {
            super(MainActivity.this, R.layout.item_organization);
        }

        @Override
        protected void setUpView(Organization organization, View view) {
            FindView.textView(view, R.id.title).setText(organization.getName());
            FindView.imageView(view, R.id.icon).setImageBitmap(organizationIcons.get(organization));
        }
    }

}