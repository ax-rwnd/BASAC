package d0020e.basac;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import d0020e.basac.Bluetooth.BluetoothArduino;
import d0020e.basac.Bluetooth.BluetoothClient;

/**
 * Created by Sebastian on 04/12/2015.
 *
 * TODO: Do not trigger warnings/collect data if statecontroller is set not to monitor to user
 */
public class StateController extends Service implements Observer {
    public static boolean serviceRunning = false;
    public static boolean bluetoothRunning = false;
    private static String TAG = "StateController";
    private static MotionSensor mMotionSensor;
    private static BluetoothClient mBluetoothClient;
    private static BluetoothArduino mBluetoothArduino;

    public static boolean warningDialog = false;
    private static boolean[] warningState = new boolean[8];

    private JSONData json;

    private static long last_update = 0;
    private Context mContext;

    private CountDownThread mCDThread;

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

    public StateController(Context c) {
        Log.d(TAG, "Constructor(Context)");
        mContext = c;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        last_update = sharedPref.getLong("last_update", System.currentTimeMillis());
        json = new JSONData();
        mMotionSensor = new MotionSensor(mContext);
        DataModel.getInstance().deleteObservers();
        DataModel.getInstance().addObserver(this);
    }

    /**
     * Called when starting StateController as a service
     */
    public StateController() {
        super();
        Log.d(TAG, "Constructor()");
    }

    public void startBluetoothConnection() {
        StateController.bluetoothRunning = true;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (sharedPref.getBoolean("start_bluetooth_arduino", false)) {
            // TODO: Make sure ArduinoReciever is working
            if (mBluetoothArduino == null) {
                /*  Blue tooth device address
                -- "00:06:66:08:5F:6F"
                "00:06:66:73:E7:81"
                "00:06:66:08:E7:D7"
                "20:FA:BB:01:98:3F"
                "20:FA:BB:01:9A:FB"
                "20:FA:BB:01:9C:17"*/
                mBluetoothArduino = new BluetoothArduino(mContext, "00:06:66:08:5F:6F");
            } else {
                Log.d(TAG, "Bluetooth already connected");
                Toast.makeText(mContext, "Already connected, try disconnecting or restarting service", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mBluetoothClient == null || mBluetoothClient.getState() == BluetoothClient.STATE_NONE) {
                mBluetoothClient = new BluetoothClient(mContext);
            } else {
                Log.d(TAG, "Bluetooth already connected");
                Toast.makeText(mContext, "Already connected, try disconnecting or restarting service", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void stopBluetoothConnection() {
        StateController.bluetoothRunning = false;
        // Cancel bluetooth notification
        NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(DataStore.NOTIFICATION_BLUETOOTHCLIENT);

        if (mBluetoothArduino != null) {
            mBluetoothArduino.stop();
            mBluetoothArduino = null;
        }
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
        DataStore ds = (DataStore)getApplication();
        ds.mState = new StateController(ds);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this;

        DataStore ds = (DataStore)getApplication();
        ds.mState = this;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        last_update = sharedPref.getLong("last_update", System.currentTimeMillis());
        boolean startBluetooth = sharedPref.getBoolean("start_bluetooth", false);

        DataModel.getInstance().deleteObservers();
        if (intent != null) {
            String stopString = intent.getStringExtra("STOP");
            if (stopString != null && stopString.length() > 0) {
                Log.d(TAG, "stop: " + stopString);
                if (stopString.equals("BLUETOOTH")) {
                    stopBluetoothConnection();
                    // Set preference
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                    editor.putBoolean("start_bluetooth", false);
                    editor.putBoolean("start_bluetooth_arduino", false);
                    editor.apply();
                    startBluetooth = false;
                } else if (stopString.equals("STOP")) {
                    stop();
                    return START_NOT_STICKY;
                }
            }
            String startString = intent.getStringExtra("START");
            if (startString != null && startString.length() > 0) {
                Log.d(TAG, "start: " + startString);
                if (startString.equals("BLUETOOTH")) {
                    startBluetooth = true;
                }
                /*if (!StateController.serviceRunning) {
                    return START_NOT_STICKY;
                }*/
            }
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);
        }

        if (startBluetooth) {
            startBluetoothConnection();
        }

        // Show notification when service is running
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("BASAC")
                .setContentText("BASAC service started")
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(
                        PendingIntent.getActivity(
                                mContext,
                                0,
                                new Intent(mContext, HomeScreenActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT
                        )
                )
                .addAction(
                        R.drawable.ic_notifications_black_24dp,
                        "Stop",
                        PendingIntent.getService(
                                mContext,
                                0,
                                new Intent(mContext, StateController.class)
                                        .putExtra("STOP", "STOP"),
                                PendingIntent.FLAG_UPDATE_CURRENT));

        NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(DataStore.NOTIFICATION_SERVICE_RUNNING, mBuilder.build());

        json = new JSONData();
        if (mMotionSensor != null) {
            mMotionSensor.sm.unregisterListener(mMotionSensor);
            mMotionSensor = null;
        }

        mMotionSensor = new MotionSensor(this);
        DataModel.getInstance().deleteObservers();
        DataModel.getInstance().addObserver(this);

        Log.d(TAG, "Service starting");
        StateController.serviceRunning = true;

        return START_STICKY;
    }

    public synchronized void stop() {
        Log.d(TAG, "stop()");
        DataModel.getInstance().deleteObserver(this);
        stopBluetoothConnection();
        if (mMotionSensor != null && mMotionSensor.sm != null) {
            mMotionSensor.sm.unregisterListener(mMotionSensor);
        }

        NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(DataStore.NOTIFICATION_SERVICE_RUNNING);

        StateController.serviceRunning = false;

        super.stopSelf();
    }

    /*public boolean stopService(Intent name) {
        Log.d(TAG, "stopService()");
        DataModel.getInstance().deleteObserver(this);
        stopBluetoothConnection();
        if (mMotionSensor != null && mMotionSensor.sm != null) {
            mMotionSensor.sm.unregisterListener(mMotionSensor);
        }

        NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(DataStore.NOTIFICATION_SERVICE_RUNNING);

        StateController.serviceRunning = false;

        return super.stopService(name);
    }*/

    public long getLastUpdate() {
        return last_update;
    }

    public static void setWarningState(int warningId, boolean state) {
        warningState[warningId] = state;
    }
    public static boolean getWarningState(int warningId) {
        return warningState[warningId];
    }

    private void showWarning(int warningId) {
        if (mBluetoothArduino != null) {
            mBluetoothArduino.turnonvibe();
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (pref.getBoolean("settings_warning_show_dialog", false)) {
            Log.d(TAG, "Showing warning dialog");
            Intent intent = new Intent(mContext, WarningDialogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("IncidentId", warningId);
            mContext.startActivity(intent);
        }

        if (pref.getBoolean("settings_warning_show_notification", true)) {
            // TODO: Send data back to arduino?
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setOngoing(true)
                    .setContentIntent(PendingIntent.getActivity(
                            mContext,
                            DataStore.NOTIFICATION_WARNING + warningId,
                            new Intent(mContext, WarningActivity.class)
                                    .putExtra("warning", warningId),
                            PendingIntent.FLAG_UPDATE_CURRENT
                    ))
                    .setPriority(NotificationCompat.PRIORITY_MAX);

            switch (warningId) {
                case DataStore.VALUE_OXYGEN:
                    mBuilder.setContentTitle("Warning, Oxygen value!")
                            .setContentText("Oxygen value is too low!");
                    break;
                case DataStore.VALUE_ACCELEROMETER:
                    mBuilder.setContentTitle("Accelerometer")
                            .setContentText("Accelerometer triggered warning");
                    break;
                case DataStore.VALUE_CO:
                    mBuilder.setContentTitle("Carbon monoxide")
                            .setContentText("Carbon monoxide levels are too high!");
                    break;
                case DataStore.VALUE_AIRPRESSURE:
                    mBuilder.setContentTitle("Air pressure")
                            .setContentText("Air pressure");
                    break;
                case DataStore.VALUE_HEARTRATE:
                    mBuilder.setContentTitle("Heart rate")
                            .setContentText("Heart rate");
                    break;
                case DataStore.VALUE_ENV_TEMPERATURE:
                    mBuilder.setContentTitle("Environment temperature")
                            .setContentText("Environment temperature");
                    break;
                case DataStore.VALUE_SKIN_TEMPERATURE:
                    mBuilder.setContentTitle("Skin temperature")
                            .setContentText("Skin temperature");
                    break;
                default:
                    mBuilder.setContentTitle("Warning!")
                            .setContentText("Unspecified warning!");
            }

            NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(DataStore.NOTIFICATION_WARNING + warningId, mBuilder.build());
        }
        if (pref.getBoolean("settings_warning_automatic_report", true)) {
            if (mCDThread == null) {
                mCDThread = new CountDownThread(StateController.this);
            }
            mCDThread.add(warningId, pref.getInt("settings_warning_report_timeout", 10) * 1000);
            if (!mCDThread.isAlive()) {
                mCDThread.start();
            }
        }
    }

    @Override
    /**
     * Is called when Datamodel is updated. checks if any thresholds are exceeded and subsequently
     * sets warning status/flags to their proper alert level
     * TODO: save json data to cache file for ccn-lite
     */
    public void update(Observable observable, Object data) {
        update();
    }

    public void update() {
        ArrayList<Integer> warnings = new ArrayList<>();
        Log.d(TAG, "Data updated");
        last_update = System.currentTimeMillis();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("last_update", last_update);
        editor.apply();

        // Oxygen
        if(DataModel.getInstance().getValue(DataStore.VALUE_OXYGEN) < DataStore.THRESHOLD_OXYGEN) {
            warnings.add(DataStore.VALUE_OXYGEN);
        }
        // Heart rate
        if (DataModel.getInstance().getValue(DataStore.VALUE_HEARTRATE) < DataStore.THRESHOLD_HEARTRATE_LOW ||
                DataModel.getInstance().getValue(DataStore.VALUE_HEARTRATE) > DataStore.THRESHOLD_HEARTRATE_HIGH) {
            warnings.add(DataStore.VALUE_HEARTRATE);
        }
        // Environment temperature
        if (DataModel.getInstance().getValue(DataStore.VALUE_ENV_TEMPERATURE) > Integer.parseInt(pref.getString("threshold_env_temperature_max", String.valueOf(DataStore.THRESHOLD_ENV_TEMPERATURE_HIGH))) ||
                DataModel.getInstance().getValue(DataStore.VALUE_ENV_TEMPERATURE) < Integer.parseInt(pref.getString("threshold_env_temperature_min", String.valueOf(DataStore.THRESHOLD_ENV_TEMPERATURE_LOW)))) {
            warnings.add(DataStore.VALUE_ENV_TEMPERATURE);
        }
        // Skin temperature
        if (DataModel.getInstance().getValue(DataStore.VALUE_SKIN_TEMPERATURE) > Integer.parseInt(pref.getString("threshold_skin_temperature_max", String.valueOf(DataStore.THRESHOLD_SKIN_TEMPERATURE_HIGH))) ||
                DataModel.getInstance().getValue(DataStore.VALUE_SKIN_TEMPERATURE) < Integer.parseInt(pref.getString("threshold_skin_temperature_min", String.valueOf(DataStore.THRESHOLD_SKIN_TEMPERATURE_LOW)))) {
            warnings.add(DataStore.VALUE_SKIN_TEMPERATURE);
        }
        // Accelerometer
        if(DataModel.getInstance().getValue(DataStore.VALUE_ACCELEROMETER) < Integer.parseInt(pref.getString("accelerometer_low", String.valueOf(DataStore.THRESHOLD_ACCELEROMETER_LOW))) ||
                DataModel.getInstance().getValue(DataStore.VALUE_ACCELEROMETER) > Integer.parseInt(pref.getString("accelerometer_high", String.valueOf(DataStore.THRESHOLD_ACCELEROMETER_HIGH)))) {
            json.putData("accelerometer", DataModel.getInstance().getValue(DataStore.VALUE_ACCELEROMETER));
            warnings.add(DataStore.VALUE_ACCELEROMETER);
        }
        // Air pressure
        if (DataModel.getInstance().getValue(DataStore.VALUE_AIRPRESSURE) < DataStore.THRESHOLD_AIRPRESSURE_LOW ||
                DataModel.getInstance().getValue(DataStore.VALUE_AIRPRESSURE) > DataStore.THRESHOLD_AIRPRESSURE_HIGH) {
            warnings.add(DataStore.VALUE_AIRPRESSURE);
        }
        // Carbon monoxide
        if (DataModel.getInstance().getValue(DataStore.VALUE_CO) > Integer.parseInt(pref.getString("threshold_co_max", String.valueOf(DataStore.THRESHOLD_CO)))) {
            warnings.add(DataStore.VALUE_CO);
        }
        // Display warnings
        for (int warningId : warnings) {
            Log.d(TAG, "warning: " + warningId + " triggered!");
            if (!warningState[warningId]) {
                showWarning(warningId);
                warningState[warningId] = true;
            }
        }

        // update JSON data
        // TODO: exclude sensitive data
        json.putData("oxygen", DataModel.getInstance().getValue(DataStore.VALUE_OXYGEN));
        json.putData("temperature", DataModel.getInstance().getValue(DataStore.VALUE_ENV_TEMPERATURE));
        json.putData("heartrate", DataModel.getInstance().getValue(DataStore.VALUE_HEARTRATE));
        json.putData("airpressure", DataModel.getInstance().getValue(DataStore.VALUE_AIRPRESSURE));
        json.putData("humidity", DataModel.getInstance().getValue(DataStore.VALUE_HUMIDITY));
        json.putData("co", DataModel.getInstance().getValue(DataStore.VALUE_CO));
        json.logJSON();

        if (pref.getBoolean("pref_key_settings_datalog", false)) {
            FileOutputStream outputStream;
            FileInputStream inputStream;
            try {
                String line;
                outputStream = mContext.openFileOutput("data.log", Context.MODE_APPEND);

                line = json.toString() + "\n";

                Log.d("Files Directory", String.valueOf(mContext.getFilesDir()));
                Log.d(TAG, "append:" + line);

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
                Log.d(TAG, "Filesize: " + sb.length() + "B, contains: " + sb.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Cleanup json data
        json.removeData("accelerometer");
    }

    public void cancelCountDown(int warningId) {
        if (mCDThread != null) {
            mCDThread.remove(warningId);
        }
    }
    // TODO: Add time left on notification
    private class CountDownThread extends Thread {
        private static final String TAG = "CountDownThread";
        TreeMap<Long, Integer> countDown;
        WeakReference<StateController> mStateController;

        public CountDownThread(StateController s) {
            countDown = new TreeMap<>();
            mStateController = new WeakReference<>(s);
        }
        public void add(int warningId, int timeOut) {
            countDown.put(System.currentTimeMillis() + timeOut, warningId);
        }
        public void remove(int warningId) {
            countDown.values().remove(warningId);
            Log.d(TAG, countDown.toString());
        }
        public void run() {
            while(countDown.size() > 0) {
                Log.d(TAG, "run()");
                try {
                    Thread.sleep(1000);
                    if (countDown.size() > 0 && countDown.firstKey() < System.currentTimeMillis()) {
                        Looper.prepare();
                        Log.d(TAG, "Remove countdown for " + countDown.get(countDown.firstKey()));
                        NotificationManager mNotifyMgr = (NotificationManager) mStateController.get().mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotifyMgr.cancel(DataStore.NOTIFICATION_WARNING + countDown.get(countDown.firstKey()));

                        StateController.setWarningState(countDown.get(countDown.firstKey()), false);
                        UserIncidentReport accidentReport = new UserIncidentReport(mStateController.get().mContext, countDown.get(countDown.firstKey()), "auto-report");
                        countDown.remove(countDown.firstKey());
                        accidentReport.submitReport();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            Log.d(TAG, "run() end");
            mStateController.get().mCDThread.cancel();
            mStateController.get().mCDThread = null;
        }

        /**
         * Stop all pending countdowns
         */
        public void cancel() {
            Log.d(TAG, "cancel()");
        }

    }

}
