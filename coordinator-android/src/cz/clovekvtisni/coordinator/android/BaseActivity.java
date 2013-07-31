package cz.clovekvtisni.coordinator.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.fhucho.android.workers.WorkingIndicator;

/**
 * Created with IntelliJ IDEA.
 * User: tomucha
 * Date: 13.05.13
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
public class BaseActivity extends SherlockFragmentActivity implements WorkingIndicator {

    protected void onCreate(Bundle state) {
        super.onCreate(state);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getSupportActionBar();
    }

    protected void showHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_menu_help);
        builder.setTitle(R.string.menu_help);

        builder.setMessage(R.string.help_general);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private int workingCount = 0;

    @Override
    public void setWorking(boolean working) {
        if (working) {
            workingCount++;
        } else {
            workingCount--;
        }
        if (workingCount > 0) {
            setSupportProgressBarIndeterminate(true);
            setSupportProgressBarIndeterminateVisibility(true);
        } else {
            setSupportProgressBarIndeterminate(false);
            setSupportProgressBarIndeterminateVisibility(false);
        }
    }

}