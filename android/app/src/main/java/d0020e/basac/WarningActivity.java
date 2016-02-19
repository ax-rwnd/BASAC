package d0020e.basac;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.text.DateFormat;

public class WarningActivity extends AppCompatActivity {

    private DataStore ds;
    private int warningId;
    private String alt1, alt2, alt3;

    private AlertDialog alertDialog;
    private JSONData reportJson;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_warning);

        this.alt1 = "case 1";
        this.alt2 = "case 2";
        this.alt3 = "case 3";

        ds = (DataStore)getApplication();

        if(ds.mState.alertDialog != null) {
            this.alertDialog = ds.mState.alertDialog;
            this.alertDialog.cancel();
        }
        reportJson = new JSONData();
        RelativeLayout layout = (RelativeLayout) View.inflate(this, R.layout.content_warning, null);

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

      /*  Button testbutton1 = new Button(this);
        testbutton1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        testbutton1.setText("test");
        layout.addView(testbutton1); */

    }
    private void submitReport(int typeOfAccident, String optionChosen) {
        UserIncidentReport accidentReport = new UserIncidentReport(warningId, optionChosen);
        reportJson.put("AccidentTimeStamp", accidentReport.getTimeStamp());
        reportJson.put("AccidentType", accidentReport.getType());
        reportJson.put("AccidentMessage", accidentReport.getReportMessage());
        reportJson.logJSON();
        Log.d("JSON REPORT", reportJson.toString());

        String filename = "report:"+accidentReport.getTimeStamp()+".txt";
        String string = accidentReport.getTimeStamp()+"::"+accidentReport.getType()+"::"+accidentReport.getReportMessage();
        FileOutputStream outputStream;

        try{
            outputStream = getApplication().openFileOutput(filename, Context.MODE_WORLD_READABLE); //TODO: Dont use this! (very dangerous)
            outputStream.write(string.getBytes());
            outputStream.close();
            Log.d("Files Directory Report", String.valueOf(getApplication().getFilesDir()));
            Log.d("Content", string);

        } catch (Exception e) {
            e.printStackTrace();
        }

        alertDialog = new AlertDialog.Builder(WarningActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(accidentReport.getReportMessage()+" at "+ DateFormat.getInstance().format(accidentReport.getTimeStamp()));
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
