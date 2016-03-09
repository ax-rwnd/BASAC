package d0020e.basac;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
    public static final int NOTIFICATION_WARNING = 4;   // 4-11
    // Bluetooth
    public static final int BLUETOOTH_REQUEST_CODE = 1;
    public static final int BLUETOOTH_RESULT_DEVICE = 2;
    // Value IDs
    public static final int VALUE_OXYGEN = 0;
    public static final int VALUE_ACCELEROMETER = 1;
    public static final int VALUE_ENV_TEMPERATURE = 2;
    public static final int VALUE_HEARTRATE = 3;
    public static final int VALUE_AIRPRESSURE = 4;
    public static final int VALUE_HUMIDITY = 5;
    public static final int VALUE_CO = 6;
    public static final int VALUE_SKIN_TEMPERATURE = 7;

    // Threshold values
    public static int THRESHOLD_OXYGEN = 18;
    public static int THRESHOLD_ACCELEROMETER_LOW = 2;
    public static int THRESHOLD_ACCELEROMETER_HIGH = 40;
    public static int THRESHOLD_ENV_TEMPERATURE_LOW = 4;
    public static int THRESHOLD_ENV_TEMPERATURE_HIGH = 50;
    public static int THRESHOLD_SKIN_TEMPERATURE_LOW = 10;
    public static int THRESHOLD_SKIN_TEMPERATURE_HIGH = 35;
    public static int THRESHOLD_HEARTRATE_LOW = 20;
    public static int THRESHOLD_HEARTRATE_HIGH = 200;
    public static int THRESHOLD_AIRPRESSURE_LOW = 60000;
    public static int THRESHOLD_AIRPRESSURE_HIGH = 150000;
    public static int THRESHOLD_CO = 30;

    public static StateController mState;

    /*Handler.Callback realCallback = null;
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
    }*/


    @Override
    public void onCreate() {
        // TODO: Add values to match arduino data
        super.onCreate();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        // Set default values for settings
        PreferenceManager.setDefaultValues(this, R.xml.preference_main, false);

        int value_oxygen = sharedPref.getInt("data_"+DataStore.VALUE_OXYGEN, 21);
        int value_env_temperature = sharedPref.getInt("data_"+DataStore.VALUE_ENV_TEMPERATURE, 20);
        int value_heart_rate = sharedPref.getInt("data_"+DataStore.VALUE_HEARTRATE, 60);
        int value_air_pressure = sharedPref.getInt("data_"+DataStore.VALUE_AIRPRESSURE, 101000);
        int value_humidity = sharedPref.getInt("data_"+DataStore.VALUE_HUMIDITY, 70);
        int value_co = sharedPref.getInt("data_"+DataStore.VALUE_CO, 0);
        int value_skin_temperature = sharedPref.getInt("data_"+DataStore.VALUE_SKIN_TEMPERATURE, 20);

        DataModel.getInstance().addValue(DataStore.VALUE_OXYGEN, value_oxygen);
        DataModel.getInstance().addValue(DataStore.VALUE_ACCELEROMETER, 10);
        DataModel.getInstance().addValue(DataStore.VALUE_ENV_TEMPERATURE, value_env_temperature);
        DataModel.getInstance().addValue(DataStore.VALUE_HEARTRATE, value_heart_rate);
        DataModel.getInstance().addValue(DataStore.VALUE_AIRPRESSURE, value_air_pressure);
        DataModel.getInstance().addValue(DataStore.VALUE_HUMIDITY, value_humidity);
        DataModel.getInstance().addValue(DataStore.VALUE_CO, value_co);
        DataModel.getInstance().addValue(DataStore.VALUE_SKIN_TEMPERATURE, value_skin_temperature);
    }

}
