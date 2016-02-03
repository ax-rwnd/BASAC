package d0020e.basac;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
        this.warningState[warningId] = false;
    }

    @Override
    /**
     * Is called when Datamodel is updated. checks if any thresholds are exceeded and subsequently
     * sets warning status/flags to their proper alert level
     * TODO: Log values
     */
    public void update(Observable observable, Object data) {
        int warningId = -1;
        Log.d(TAG, "Data updated");
        if((DataModel.getInstance().getValue(DataStore.VALUE_TESTVALUE) > 30)) {
            this.warningState[DataStore.VALUE_TESTVALUE] = true;
            warningId = DataStore.VALUE_TESTVALUE;
        }
        if((DataModel.getInstance().getValue(1) < 2) && (this.warningState[1]!=true)) {
            this.warningState[DataStore.WARNING_ACCELEROMETER] = true;
            warningId = DataStore.WARNING_ACCELEROMETER;
            Log.d("Accelerometer", "YOU'RE FALLIN!");
        }
        if (warningId != -1) {
            showWarning(warningId);
        }
        // update JSON data
        json.put("test value", DataModel.getInstance().getValue(DataStore.VALUE_TESTVALUE));
        json.logJSON();
    }
}
