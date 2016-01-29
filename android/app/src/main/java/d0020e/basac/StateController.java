package d0020e.basac;

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
    private HomeScreenActivity mContext;

    private Boolean warningDialog = false;
    private Boolean warningState = false;

    public StateController() {
        DataModel.getInstance().addObserver(this);
    }

    public void setContext(Context c) {
        this.mContext = (HomeScreenActivity)c;
    }

    private void showWarning() {
        Log.d(TAG, "Warning");

        int notificationId = 1;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Warning")
                .setContentText("Test value is too high!");
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        Intent resultIntent = new Intent(mContext, WarningActivity.class);
        resultIntent.putExtra("warning", 1);
        resultIntent.putExtra("notificationId", notificationId);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                mContext,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(notificationId, mBuilder.build());

        if (!this.warningDialog) {
            Log.d(TAG, "Showing warning dialog");
            this.warningDialog = true;
        }
        this.warningState = false;
    }

    @Override
    /**
     * Is called when Datamodel is updated. checks if any thresholds are exceeded and subsequently
     * sets warning status/flags to their proper alert level
     */
    public void update(Observable observable, Object data) {
        Log.d("StateController", "Data updated");
        if((DataModel.getInstance().getValue(0) > 30)) {
            this.warningState = true;
        }
        if (this.warningState) {
            showWarning();
        }
    }
}
