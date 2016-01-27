package d0020e.basac;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import java.io.Serializable;
import java.util.Observable;

/**
 * The Datamodel, holds the data recieved from the "Väst"
 */
public class DataModel extends Observable implements Serializable {
    private static final String TAG = "DataModel";

    //variables saved on the static object to bypass the throwing away of essential data on pause of activity?
    private static int testValue;
    private static boolean warning;

    private static Context mContext;

    public DataModel() {
        testValue = 0;
        warning = false;
        Log.d(TAG, "DataModel constructor");
    }

    public static void setContext(Context c) {
        mContext = c;
    }

    public int getTestValue() {
        return testValue;
    }

    /* Sets value to the actual value, invoked by StateController */
    public void setTestValue(int newValue) {
        Log.d(TAG,"setTestValue("+String.valueOf(newValue)+")");
        testValue = newValue;
        setChanged();
        notifyObservers();
    }

    public void toggleWarning() {
        warning = !warning;
    }

    public boolean getWarningState() {
        return warning;
    }

    public void notifyView() {
        setChanged();
        notifyObservers();
    }

}
