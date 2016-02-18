package d0020e.basac;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WarningActivity extends AppCompatActivity {

    private DataStore ds;
    private int warningId;
    private String alt1, alt2, alt3;

    private AlertDialog alertDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.alt1 = "case 1";
        this.alt2 = "case 2";
        this.alt3 = "case 3";

        ds = (DataStore)getApplication();

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
        mNotifyMgr.cancel(DataStore.NOTIFICATION_WARNING + warningId);

        switch (warningId) {
            case DataStore.VALUE_OXYGEN:
                mWarningText.setText("Oxygen value is too low!");
                break;
            case DataStore.VALUE_ACCELEROMETER:
                mWarningText.setText("Accelerometer");
                this.alt1 = "Need Help!";
                this.alt2 = "Minor fall";
                this.alt3 = "Nothing";
                break;
            case DataStore.VALUE_AIRPRESSURE:
                mWarningText.setText("Airpressure");
                break;
            case DataStore.VALUE_CO:
                mWarningText.setText("Carbon monoxide");
                break;
            case DataStore.VALUE_HEARTRATE:
                mWarningText.setText("Heart rate");
                break;
            case DataStore.VALUE_TEMPERATURE:
                mWarningText.setText("Temperature");
                break;
            default:
                mWarningText.setText("Unknown warning id: " + warningId);
        }

        Button warningButton1 = (Button) findViewById(R.id.warning_button_1);
        warningButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitReport(warningId, alt1);
            }
        });
        warningButton1.setText(this.alt1);
        Button warningButton2 = (Button) findViewById(R.id.warning_button_2);
        warningButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitReport(warningId, alt2);
            }
        });
        warningButton2.setText(this.alt2);
        Button warningButton3 = (Button) findViewById(R.id.warning_button_3);
        warningButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitReport(warningId, alt3);
            }
        });
        warningButton3.setText(this.alt3);
    }
    //TODO: Make the reports actually submit something useful.
    private void submitReport(int typeOfAccident, String optionChosen) {
        UserIncidentReport accidentReport = new UserIncidentReport(warningId, optionChosen);

        alertDialog = new AlertDialog.Builder(WarningActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(accidentReport.getReportMessage()+" at "+accidentReport.getTimeStamp());
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
