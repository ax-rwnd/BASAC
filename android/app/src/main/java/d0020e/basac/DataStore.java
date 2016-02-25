package d0020e.basac;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by WeeDzCokie on 2016-01-28.
 */
public class DataStore extends Application {
    // Location
    public static final int LOCATION_SAFE = 0;
    public static final int LOCATION_DANGEROUS = 1;
    // Notification
    public static final int NOTIFICATION_BLUETOOTHCLIENT = 1;
    public static final int NOTIFICATION_BLUETOOTH_LOST = 2;
    public static final int NOTIFICATION_SERVICE_RUNNING = 3;
    public static final int NOTIFICATION_WARNING = 4;   // +xx
    // Bluetooth
    public static final int BLUETOOTH_REQUEST_CODE = 1;
    public static final int BLUETOOTH_RESULT_DEVICE = 2;
    // Value IDs
    public static final int VALUE_OXYGEN = 0;
    public static final int VALUE_ACCELEROMETER = 1;
    public static final int VALUE_TEMPERATURE = 2;
    public static final int VALUE_HEARTRATE = 3;
    public static final int VALUE_AIRPRESSURE = 4;
    public static final int VALUE_HUMIDITY = 5;
    public static final int VALUE_CO = 6;

    // Threshold values
    // TODO: Set these values from settings and/or from server
    public static int THRESHOLD_OXYGEN = 18;
    public static double THRESHOLD_ACCELEROMETER_LOW = 2;
    public static double THRESHOLD_ACCELEROMETER_HIGH = 30;
    public static int THRESHOLD_TEMPERATURE_LOW = 4;
    public static int THRESHOLD_TEMPERATURE_HIGH = 40;
    public static int THRESHOLD_HEARTRATE_LOW = 20;
    public static int THRESHOLD_HEARTRATE_HIGH = 180;
    public static int THRESHOLD_AIRPRESSURE_LOW = 60000;
    public static int THRESHOLD_AIRPRESSURE_HIGH = 150000;
    public static int THRESHOLD_CO = 25;

    public StateController mState;

    Handler.Callback realCallback = null;
    Handler handler  = new Handler() {
        public void handleMessage(Message msg) {
            if (realCallback != null) {
                realCallback.handleMessage(msg);
            }
        }
    };

    public Handler getHandler() {
        return handler;
    }

    public void setCallback(Handler.Callback callback) {
        this.realCallback = callback;
    }

    @Override
    public void onCreate() {
        // TODO: Add values to match arduino data
        super.onCreate();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        // Set default values for settings
        PreferenceManager.setDefaultValues(this, R.xml.preference_main, false);

        int value_oxygen = sharedPref.getInt("data_"+DataStore.VALUE_OXYGEN, 21);
        int value_temperature = sharedPref.getInt("data_"+DataStore.VALUE_TEMPERATURE, 20);
        int value_heartrate = sharedPref.getInt("data_"+DataStore.VALUE_HEARTRATE, 60);
        int value_airpressure = sharedPref.getInt("data_"+DataStore.VALUE_AIRPRESSURE, 101000);
        int value_humidity = sharedPref.getInt("data_"+DataStore.VALUE_HUMIDITY, 70);
        int value_co = sharedPref.getInt("data_"+DataStore.VALUE_CO, 0);

        DataModel.getInstance().addValue(value_oxygen);
        DataModel.getInstance().addValue(10);
        DataModel.getInstance().addValue(value_temperature);
        DataModel.getInstance().addValue(value_heartrate);
        DataModel.getInstance().addValue(value_airpressure);
        DataModel.getInstance().addValue(value_humidity);
        DataModel.getInstance().addValue(value_co);
    }

}
