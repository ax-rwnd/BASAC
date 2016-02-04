package d0020e.basac;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

/**
 * Created by WeeDzCokie on 2016-01-28.
 */
public class DataStore extends Application {
    // Notification
    public static final int NOTIFICATION_WARNING = 1;
    public static final int NOTIFICATION_BLUETOOTHCLIENT = 2;
    public static final int NOTIFICATION_BLUETOOTH_LOST = 3;
    // Bluetooth
    public static final int BLUETOOTH_REQUEST_CODE = 1;
    public static final int BLUETOOTH_RESULT_DEVICE = 2;
    // Value IDs
    public static final int VALUE_TESTVALUE = 0;
    public static final int VALUE_ACCELEROMETER = 1;

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
    public StateController getState() {
        return mState;
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

        int value_testValue = sharedPref.getInt("data_"+DataStore.VALUE_TESTVALUE, DataStore.VALUE_TESTVALUE);

        DataModel.getInstance().addValue(value_testValue);
        DataModel.getInstance().addValue(10);

        mState = new StateController();
    }

}
