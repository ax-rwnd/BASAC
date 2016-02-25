package d0020e.basac;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * TODO: add a timer to automatically send a report if user don't respond.
 */

public class DialogActivity extends Activity {
    private static final String TAG = "DialogActivity";
    private String reportMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        Intent i = getIntent();
        final int warningId = i.getIntExtra("IncidentId", -1);
        TextView title = (TextView)findViewById(R.id.report_title);
        TextView description = (TextView)findViewById(R.id.report_description);
        switch (warningId) {
            case DataStore.VALUE_ACCELEROMETER:
                reportMessage = "Accelerometer";
                description.setText("Have you fallen?");
                break;
            case DataStore.VALUE_OXYGEN:
                reportMessage = "Oxygen too low";
                description.setText("Oxygen value is too low");
                break;
            default:
                reportMessage = "Unknown warning";
        }
        title.setText(reportMessage);

        Button cancel = (Button)findViewById(R.id.cancel_report);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StateController.warningDialog = false;
                finish();
            }
        });
        Button send = (Button)findViewById(R.id.send_report);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Report sent", Toast.LENGTH_SHORT).show();
                // Stuff to send report
                NotificationManager mNotifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotifyMgr.cancel(DataStore.NOTIFICATION_WARNING + warningId);
                UserIncidentReport accidentReport = new UserIncidentReport(DialogActivity.this, warningId, reportMessage);
                accidentReport.submitReport();
                finish();
            }
        });
    }

    @Override
    public void finish() {
        StateController.warningDialog = false;
        super.finish();
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}
