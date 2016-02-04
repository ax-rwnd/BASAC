package d0020e.basac;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Sebastian on 04/12/2015.
 */
public class StateController implements Observer {
    private static String TAG = "StateController";
    private Context mContext;

    private boolean warningDialog = false;
    //private boolean warningState = false;

    private boolean[] warningState;

    private JSONData json;

    public static final int ACCIDENT_TEST = 0;
    public static final int ACCIDENT_FALL = 1;

    public StateController() {
        json = new JSONData();
        warningState = new boolean[5];
        warningState[1] = false; //sets fallaccident to false startup.
        DataModel.getInstance().addObserver(this);
    }

    public void setContext(Context c) {
        this.mContext = c;
    }

    private void showWarning(int warningId) {
        Log.d(TAG, "Warning");

        // TODO: Send data back to arduino?
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(
                        mContext,
                        0,
                        new Intent(mContext, WarningActivity.class)
                                .putExtra("warning", warningId),
                        PendingIntent.FLAG_UPDATE_CURRENT
                ))
                .setPriority(NotificationCompat.PRIORITY_MAX);

        switch (warningId) {
            case DataStore.VALUE_TESTVALUE:
                mBuilder.setContentTitle("Warning, Test value")
                        .setContentText("Test value too high!");
                break;
            default:
                mBuilder.setContentTitle("Warning!")
                        .setContentText("Unspecified warning!");
        }

        NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(DataStore.NOTIFICATION_WARNING, mBuilder.build());

        if (!this.warningDialog) {
            Log.d(TAG, "Showing warning dialog");
            this.warningDialog = true;
        }
    }

    @Override
    /**
     * Is called when Datamodel is updated. checks if any thresholds are exceeded and subsequently
     * sets warning status/flags to their proper alert level
     * TODO: Log values
     */
    public void update(Observable observable, Object data) {
        int warningId = -1;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        //Log.d(TAG, "Data updated");
        if((DataModel.getInstance().getValue(DataStore.VALUE_TESTVALUE) > 30) && !this.warningState[DataStore.VALUE_TESTVALUE]) {
            this.warningState[DataStore.VALUE_TESTVALUE] = true;
            warningId = DataStore.VALUE_TESTVALUE;
            incidentReport(ACCIDENT_TEST);
        }
        //Triggers fall if "falling", not already triggered and inside dangerzone.
        if((DataModel.getInstance().getValue(1) < 2) && !this.warningState[DataStore.VALUE_ACCELEROMETER]
                && pref.getBoolean("pref_key_settings_in_danger_zone", false)) {
            this.warningState[DataStore.VALUE_ACCELEROMETER] = true;
            warningId = DataStore.VALUE_ACCELEROMETER;
            Log.d("Accelerometer", "YOU'RE FALLIN!");
            incidentReport(ACCIDENT_FALL);

        }
        if (warningId != -1) {
            showWarning(warningId);
        }
        // update JSON data
        json.put("test_value", DataModel.getInstance().getValue(DataStore.VALUE_TESTVALUE));
        //json.logJSON();

        if (pref.getBoolean("pref_key_settings_datalog", false)) {
            FileOutputStream outputStream;
            FileInputStream inputStream;
            try {
                String line;
                outputStream = mContext.openFileOutput("data.log", Context.MODE_APPEND);

                line = json.getJSON().toString() + "\n";

                Log.d(TAG, line);

                outputStream.write(line.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                inputStream = mContext.openFileInput("data.log");
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                Log.d(TAG, "File contains: " + sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void incidentReport(int typeOfIncident) {
        String incidentType;
        switch (typeOfIncident) {
            case ACCIDENT_TEST:
                incidentType = "Vest value exceeded";
                break;
            case ACCIDENT_FALL:
                incidentType = "Fall accident";
                break;
            default:
                incidentType = "none";
                //kek
        }
        new AlertDialog.Builder(mContext)
                .setTitle(incidentType)
                .setMessage("Have you fallen?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
