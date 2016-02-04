package d0020e.basac;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Date;
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

    private long last_update = 0;

    public StateController() {
        json = new JSONData();
        warningState = new boolean[5];
        DataModel.getInstance().addObserver(this);
    }

    public void setContext(Context c) {
        this.mContext = c;
        new MotionSensor(mContext);
    }

    public long getLastUpdate() {
        return last_update;
    }

    public void setWarningState(int warningId, boolean state) {
        warningState[warningId] = state;
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
            case DataStore.VALUE_ACCELEROMETER:
                mBuilder.setContentTitle("Accelerometer")
                        .setContentText("Accelerometer triggered warning");
                break;
            default:
                mBuilder.setContentTitle("Warning!")
                        .setContentText("Unspecified warning!");
        }

        NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(DataStore.NOTIFICATION_WARNING+warningId, mBuilder.build());

        if (!this.warningDialog) {
            Log.d(TAG, "Showing warning dialog");
            this.warningDialog = true;
        }
    }

    @Override
    /**
     * Is called when Datamodel is updated. checks if any thresholds are exceeded and subsequently
     * sets warning status/flags to their proper alert level
     * TODO: save json data to cache file for ccn-lite
     */
    public void update(Observable observable, Object data) {
        int warningId = -1;
        Log.d(TAG, "Data updated");
        last_update = System.currentTimeMillis();

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putLong("last_update", last_update);
        editor.apply();

        if((DataModel.getInstance().getValue(DataStore.VALUE_TESTVALUE) > 30) && !this.warningState[DataStore.VALUE_TESTVALUE]) {
            this.warningState[DataStore.VALUE_TESTVALUE] = true;
            showWarning(DataStore.VALUE_TESTVALUE);
        }
        if((DataModel.getInstance().getValue(DataStore.VALUE_ACCELEROMETER) < MotionSensor.threshold) && !this.warningState[DataStore.VALUE_ACCELEROMETER]) {
            this.warningState[DataStore.VALUE_ACCELEROMETER] = true;
            json.put("accelerometer", DataModel.getInstance().getValue(DataStore.VALUE_ACCELEROMETER));
            showWarning(DataStore.VALUE_ACCELEROMETER);
        }
        if (warningId != -1) {
            showWarning(warningId);
        }
        // update JSON data
        json.put("test_value", DataModel.getInstance().getValue(DataStore.VALUE_TESTVALUE));
        json.logJSON();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (pref.getBoolean("pref_key_settings_datalog", false)) {
            FileOutputStream outputStream;
            FileInputStream inputStream;
            try {
                String line;
                outputStream = mContext.openFileOutput("data.log", Context.MODE_APPEND);

                line = json.toString() + "\n";

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
        // Cleanup json data
        json.remove("accelerometer");
    }
}
