package d0020e.basac;

import android.app.DialogFragment;
import android.app.FragmentManager;
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
    private DataModel dataModel;
    private Context mContext;

    private Boolean warningDisplay = false;

    public StateController(DataModel dataModel) {
        this.dataModel = dataModel;
        this.dataModel.addObserver(this);
    }

    public void setContext(Context c) {
        this.mContext = c;
    }

    public void run() {
        if((dataModel.getTestValue() > 30) && !dataModel.getWarningState()) {
            dataModel.toggleWarning();
        }
        if (dataModel.getWarningState()) {
            // Display a warning
            showWarning();
        }
        Log.d("StateController", "is running");
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

        if (!warningDisplay) {
            this.warningDisplay = true;
            //DialogFragment warningDialog = new WarningDialog();
            //warningDialog.show(mContext.getSupportFragmentManager(), "warning");
        }

    }

    @Override
    /* Is called when Datamodel is updated. checks if any thresholds are exceeded and subsequently
    * sets warning status/flags to their proper alert level */
    public void update(Observable observable, Object data) {
        Log.d("StateController", "data updated");
        if((dataModel.getTestValue() > 30) && !dataModel.getWarningState()) {
            dataModel.toggleWarning();
            showWarning();
        }
    }
}
