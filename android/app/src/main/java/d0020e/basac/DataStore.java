package d0020e.basac;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by WeeDzCokie on 2016-01-28.
 */
public class DataStore extends Application {
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
        // TODO: Set values in DataModel from file
        // TODO: Add values to match arduino data
        super.onCreate();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int value_01 = sharedPref.getInt("data_01", 0);

        DataModel.getInstance().addValue(value_01);

        mState = new StateController();
    }

}
