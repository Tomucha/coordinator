package cz.clovekvtisni.coordinator.android.welcome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.fhucho.android.workers.Workers;
import com.fhucho.android.workers.simple.ActivityWorker2;
import com.google.android.gcm.GCMRegistrar;
import cz.clovekvtisni.coordinator.SecretInfo;
import cz.clovekvtisni.coordinator.android.BaseActivity;
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
import cz.clovekvtisni.coordinator.api.request.LoginRequestParams;
import cz.clovekvtisni.coordinator.api.request.UserPushTokenRequestParams;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
import cz.clovekvtisni.coordinator.api.response.LoginResponseData;
import cz.clovekvtisni.coordinator.api.response.UserByIdResponseData;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.util.ValueTool;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

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
            Lg.GCM.i("Going to register: "+SecretInfo.GCM_SENDER_ID);
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
        setWorking(true);
        Workers.load(new ConfigLoader(), new ConfigLoaderListener() {
            @Override
            public void onResult(ConfigResponse result) {
                setWorking(false);
                onOrganizationsLoaded(result.getOrganizationList());
            }

            @Override
            public void onInternetException(Exception e) {
                setWorking(false);
                UiTool.toast(R.string.error_no_internet, getApplicationContext());
                finish();
            }
	        @Override
	        public void onServerSideException(ApiCall.ApiServerSideException e) {
		        UiTool.toast(R.string.error_server, getApplicationContext());
		        finish();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_help: showHelp(); return true;
            case R.id.menu_login: showLogin(); return true;
        }
        return super.onOptionsItemSelected(item);    //To change body of overridden methods use File | Settings | File Templates.
    }

    private void showLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.menu_login);

        LayoutInflater inflater = getLayoutInflater();

        View content = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(content);
        final EditText email = (EditText) content.findViewById(R.id.email);
        final EditText password = (EditText) content.findViewById(R.id.password);



        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.ok, null);

        final AlertDialog dialog = builder.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = email.getText().toString();
                if (ValueTool.isEmpty(emailText)) {
                    email.requestFocus();
                    UiTool.toast(R.string.error_missing_value, getApplicationContext());
                    return;
                }
                String passwordText = password.getText().toString();
                if (ValueTool.isEmpty(passwordText)) {
                    password.requestFocus();
                    UiTool.toast(R.string.error_missing_value, getApplicationContext());
                    return;
                }
                LoginRequestParams p = new LoginRequestParams();
                p.setEmail(emailText);
                p.setPassword(passwordText);
                setWorking(true);
                Workers.start(new UserLoginTask(p, dialog), MainActivity.this);
            }
        });

        TextView forgotten = (TextView) content.findViewById(R.id.forgotten_password);
        forgotten.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isWorking()) return;
                        String emailText = email.getText().toString();
                        if (ValueTool.isEmpty(emailText)) {
                            email.requestFocus();
                            UiTool.toast(R.string.error_missing_value, getApplicationContext());
                            return;
                        }
                        setWorking(true);
                        LoginRequestParams p = new LoginRequestParams();
                        p.setEmail(emailText);
                        Workers.start(new UserPasswordTask(p, dialog), MainActivity.this);
                    }
                }
        );

    }

    private class UserLoginTask extends
            ActivityWorker2<MainActivity, LoginResponseData, Exception> {

        private final LoginRequestParams params;
        private final DialogInterface dialogToClose;

        public UserLoginTask(LoginRequestParams params, DialogInterface dialogToClose) {
            this.params = params;
            this.dialogToClose = dialogToClose;
        }

        @Override
        protected void doInBackground() {
            try {
                LoginResponseData result = new ApiCalls.UserLoginCall(params).call();

                if (result.getAuthKey() != null) {
                    // auth key was changed
                    Settings.setAuthKey(result.getAuthKey());
                }

                try {
                    String regId = Settings.getGcmRegistrationId();
                    UserPushTokenRequestParams params = new UserPushTokenRequestParams(regId);
                    new ApiCalls.UserPushTokenCall(params).call();
                    Lg.GCM.d("Successfully uploaded registration id to the server.");
                } catch (ApiCall.ApiCallException e) {
                    Lg.GCM.w("Uploading registration id to the server unsuccessful.", e);
                }
                getListenerProxy().onSuccess(result);
            } catch (final ApiCall.ApiServerSideException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setWorking(false);
                        UiTool.showServerError(e, getApplicationContext());
                    }
                });

            } catch (ApiCall.ApiCallException e) {
                getListenerProxy().onException(e);
            }
        }

        @Override
        public void onSuccess(LoginResponseData result) {
            setWorking(false);
            UiTool.toast(R.string.message_logged, getApplicationContext());
            onUserLoaded(result.getUser());
            dialogToClose.dismiss();
        }

        @Override
        public void onException(Exception e) {
            setWorking(false);
            Lg.APP.e("Chyba "+e, e);
            Toast.makeText(getActivity(), "Chyba", Toast.LENGTH_SHORT).show();
            dialogToClose.dismiss();
        }

    }

    private class UserPasswordTask extends ActivityWorker2<MainActivity, Void, Exception> {

        private final LoginRequestParams params;

        public UserPasswordTask(LoginRequestParams params, DialogInterface dialogToClose) {
            this.params = params;
        }

        @Override
        protected void doInBackground() {
            try {
                new ApiCalls.UserPasswordCall(params).call();
                getListenerProxy().onSuccess(null);
            } catch (final ApiCall.ApiServerSideException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UiTool.showServerError(e, getApplicationContext());
                    }
                });

            } catch (ApiCall.ApiCallException e) {
                getListenerProxy().onException(e);
            }
        }

        @Override
        public void onSuccess(Void result) {
            UiTool.toast(R.string.message_password_changed, getApplicationContext());
            setWorking(false);
        }

        @Override
        public void onException(Exception e) {
            Lg.APP.e("Chyba " + e, e);
            Toast.makeText(getActivity(), "Chyba", Toast.LENGTH_SHORT).show();
            setWorking(false);
        }

    }


    private void loadMyself() {
        setWorking(true);
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
            setWorking(false);
            onUserLoaded(result.getFirst());
        }

        @Override
        public void onException(Exception e) {
            setWorking(false);
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
            super(MainActivity.this, R.layout.item_with_icon);
        }

        @Override
        protected void setUpView(Organization organization, View view) {
            FindView.textView(view, R.id.title).setText(organization.getName());
            FindView.textView(view, R.id.short_description).setText(organization.getShortDescription());
            FindView.imageView(view, R.id.icon).setImageBitmap(organizationIcons.get(organization));
        }
    }

}