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
import android.widget.Toast;

import java.text.DateFormat;

/**
 * The activity for submitting the incident-reports.
 */

public class WarningActivity extends AppCompatActivity {

    private int warningId;
    private String alt1, alt2, alt3;

    DataStore ds;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_warning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle data = getIntent().getExtras();
        TextView mWarningText = (TextView) findViewById(R.id.warning);
        warningId = data.getInt("warning");

        if (!StateController.getWarningState(warningId)) {
            Toast.makeText(this, "Report already sent", Toast.LENGTH_SHORT).show();
            finish();
        }

        ds = (DataStore)getApplication();
        ds.mState.cancelCountDown(warningId);

        this.alt1 = "case 1";
        this.alt2 = "case 2";
        this.alt3 = "case 3";

        //RelativeLayout layout = (RelativeLayout) View.inflate(this, R.layout.content_warning, null);

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(DataStore.NOTIFICATION_WARNING + warningId);

        switch (warningId) {
            case DataStore.VALUE_OXYGEN:
                mWarningText.setText(R.string.warning_oxygen);
                break;
            case DataStore.VALUE_ACCELEROMETER:
                mWarningText.setText(R.string.warning_accelerometer);
                this.alt1 = "Need Help!";
                this.alt2 = "Minor fall";
                this.alt3 = "Nothing";
                break;
            case DataStore.VALUE_AIRPRESSURE:
                mWarningText.setText(R.string.warning_air_pressure);
                break;
            case DataStore.VALUE_CO:
                mWarningText.setText(R.string.warning_carbon_monoxide);
                break;
            case DataStore.VALUE_HEARTRATE:
                mWarningText.setText(R.string.warning_heart_rate);
                break;
            case DataStore.VALUE_ENV_TEMPERATURE:
                mWarningText.setText(R.string.warning_env_temperature);
                break;
            case DataStore.VALUE_SKIN_TEMPERATURE:
                mWarningText.setText(R.string.warning_skin_temperature);
                break;
            default:
                mWarningText.setText(String.format(getString(R.string.warning_unknown), warningId));
        }

        Button warningButton1 = (Button) findViewById(R.id.warning_button_1);
        warningButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitReport(alt1);
            }
        });
        warningButton1.setText(this.alt1);
        Button warningButton2 = (Button) findViewById(R.id.warning_button_2);
        warningButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitReport(alt2);
            }
        });
        warningButton2.setText(this.alt2);
        Button warningButton3 = (Button) findViewById(R.id.warning_button_3);
        warningButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitReport(alt3);
            }
        });
        warningButton3.setText(this.alt3);

    }
    private void submitReport(String optionChosen) {
        UserIncidentReport accidentReport = new UserIncidentReport(this, warningId, optionChosen);
        accidentReport.submitReport(ds.mState);

        AlertDialog alertDialog = new AlertDialog.Builder(WarningActivity.this)
                .setTitle("Alert")
                .setMessage(accidentReport.getReportMessage()+" at "+ DateFormat.getInstance().format(accidentReport.getTimeStamp()))
                .create();
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        //to prevent user from just leaving the warning submission.
    }

}
