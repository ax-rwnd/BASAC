package d0020e.basac;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.view.View;

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
public class StateController extends Service implements Observer {
    private static String TAG = "StateController";
    private MotionSensor mMotionSensor;
    private BluetoothClient mBluetoothClient;

    private boolean warningDialog = false;
    //private boolean warningState = false;
    private boolean[] warningState;

    private JSONData json;


    public static final int ACCIDENT_TEST = 0;
    public static final int ACCIDENT_FALL = 1;

    private long last_update = 0;
    private int startId;

    //Service
    private ServiceHandler mServiceHandler;
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "ServiceHandler msg: " + msg.arg1);
        }
    }

    /**
     * TODO: Update service notification
     */
    public void startBluetoothConnection() {
        if (mBluetoothClient == null || mBluetoothClient.getState() == BluetoothClient.STATE_NONE) {
            mBluetoothClient = new BluetoothClient(this);
        } else {
            Log.d(TAG, "Bluetooth already connected");
        }
    }
    public void stopBluetoothConnection() {
        if (mBluetoothClient != null) {
            mBluetoothClient.setStop();
            mBluetoothClient.stop();
            mBluetoothClient = null;
        } else {
            Log.d(TAG, "Bluetooth not connected");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // No binder
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("StateController", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Looper serviceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public void onDestroy() {
        stop();
        Log.d(TAG, "service destroyed");
        super.onDestroy();
    }

    @Override
    /**
     * TODO: update notification when bluetooth is connected
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        last_update = sharedPref.getLong("last_update", System.currentTimeMillis());
        boolean startBluetooth = sharedPref.getBoolean("start_bluetooth", false);

        if (intent != null) {
            String stopString = intent.getStringExtra("STOP");
            if (stopString != null && stopString.length() > 0) {
                Log.d(TAG, "stop: " + stopString);
                if (stopString.equals("BLUETOOTH")) {
                    stopBluetoothConnection();
                } else if (stopString.equals("STOP")){
                    stop();
                    return 0;
                }
            }
            /*String startString = intent.getStringExtra("START");
            if (startString != null && startString.length() > 0) {
                Log.d(TAG, "start: " + startString);
                if (startString.equals("BLUETOOTH")) {
                    startBluetoothConnection();
                }
            }*/
            this.startId = startId;
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);
        }

        if (startBluetooth) {
            startBluetoothConnection();
        }

        // Show notification when service is running
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("BASAC")
                .setContentText("BASAC service started")
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(
                        PendingIntent.getActivity(
                                this,
                                0,
                                new Intent(this, HomeScreenActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT
                        )
                )
                .addAction(
                        R.drawable.ic_notifications_black_24dp,
                        "Stop",
                        PendingIntent.getService(
                                this,
                                0,
                                new Intent(this, StateController.class)
                                        .putExtra("STOP", "STOP"),
                                PendingIntent.FLAG_UPDATE_CURRENT));

        NotificationManager mNotifyMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(DataStore.NOTIFICATION_SERVICE_RUNNING, mBuilder.build());

        DataStore ds = (DataStore)getApplication();
        ds.mState = this;

        json = new JSONData();
        warningState = new boolean[5];
        mMotionSensor = new MotionSensor(this);
        DataModel.getInstance().addObserver(this);

        Log.d(TAG, "Service starting");

        return START_STICKY;
    }

    public void setContext(Context c) {
        this.mContext = c;
        new MotionSensor(mContext);
    }

    public synchronized void stop() {
        DataModel.getInstance().deleteObserver(this);
        stopBluetoothConnection();

        NotificationManager mNotifyMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(DataStore.NOTIFICATION_SERVICE_RUNNING);

        Log.d(TAG, "stop()");
        super.stopSelf();
    }

    public boolean stopService(Intent name) {
        if (mBluetoothClient != null) {
            mBluetoothClient.stop();
        }
        Log.d(TAG, "stopService()");
        return super.stopService(name);
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
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(
                        this,
                        0,
                        new Intent(this, WarningActivity.class)
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

        NotificationManager mNotifyMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(DataStore.NOTIFICATION_WARNING + warningId, mBuilder.build());

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

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putLong("last_update", last_update);
        editor.apply();

        if((DataModel.getInstance().getValue(DataStore.VALUE_TESTVALUE) > 30) && !this.warningState[DataStore.VALUE_TESTVALUE]) {
            this.warningState[DataStore.VALUE_TESTVALUE] = true;
            showWarning(DataStore.VALUE_TESTVALUE);
        }
        //Triggers fall if "falling", not already triggered and inside dangerzone.
        if((DataModel.getInstance().getValue(DataStore.VALUE_ACCELEROMETER) < MotionSensor.threshold)
                && !this.warningState[DataStore.VALUE_ACCELEROMETER]
                && pref.getBoolean("pref_key_settings_in_danger_zone", false)) {

            this.warningState[DataStore.VALUE_ACCELEROMETER] = true;
            json.put("accelerometer", DataModel.getInstance().getValue(DataStore.VALUE_ACCELEROMETER));
            showWarning(DataStore.VALUE_ACCELEROMETER);
            incidentReport(ACCIDENT_FALL);

        }
        if (warningId != -1) {
            showWarning(warningId);
        }
        // update JSON data
        json.put("test_value", DataModel.getInstance().getValue(DataStore.VALUE_TESTVALUE));
        json.logJSON();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pref.getBoolean("pref_key_settings_datalog", false)) {
            FileOutputStream outputStream;
            FileInputStream inputStream;
            try {
                String line;
                outputStream = this.openFileOutput("data.log", Context.MODE_APPEND);

                line = json.toString() + "\n";

                Log.d(TAG, line);

                outputStream.write(line.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                inputStream = this.openFileInput("data.log");
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
