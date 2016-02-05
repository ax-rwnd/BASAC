package d0020e.basac;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class WarningActivity extends AppCompatActivity {

    private DataStore ds;
    private int warningId;

    private AlertDialog alertDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ds = (DataStore)getApplicationContext();

        if(ds.mState.alertDialog != null) {
            this.alertDialog = ds.mState.alertDialog;
            this.alertDialog.cancel();
        }

        setContentView(R.layout.activity_warning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle data = getIntent().getExtras();
        TextView mWarningText = (TextView) findViewById(R.id.warning);
        warningId = data.getInt("warning");

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(DataStore.NOTIFICATION_WARNING+warningId);

        switch (warningId) {
            case DataStore.VALUE_TESTVALUE:
                mWarningText.setText("Test value is too high!");
                break;
            case DataStore.VALUE_ACCELEROMETER:
                mWarningText.setText("Accelerometer, you are falling..");
                break;
            default:
                mWarningText.setText("Unknown warning id: " + warningId);
        }

        ds = (DataStore)getApplication();

        Button warningButton1 = (Button) findViewById(R.id.warning_button_1);
        warningButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitReport(1);
            }
        });
        Button warningButton2 = (Button) findViewById(R.id.warning_button_2);
        warningButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitReport(2);
            }
        });
        Button warningButton3 = (Button) findViewById(R.id.warning_button_3);
        warningButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitReport(2);
            }
        });
    }
    //TODO: Make the reports actually submit something useful.
    private void submitReport(int typeOfAccident) {
        alertDialog = new AlertDialog.Builder(WarningActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Alert message to be shown");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ds.mState.setWarningState(warningId, false);
                        finish();
                    }
                });
        alertDialog.show();
    }

    @Override //to prevent user from just leaving the warning submission.
    public void onBackPressed() {

    }
}
