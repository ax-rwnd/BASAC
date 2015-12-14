package d0020e.basac;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.Observable;

/**
 * The Datamodel, holds the data recieved from the "Väst"
 */
public class DataModel extends Observable implements Serializable {
    private static final String TAG = "DataModel";

    private static int testValue;

    public DataModel() {
        this.testValue = 0;
        Log.d(TAG, "constructor");
    }

    public int getTestValue() {
        return testValue;
    }
    /* Sets value to the actual value, invoked by StateController */

    public void setTestValue(int newValue) {
        Log.d(TAG,"setTestValue("+String.valueOf(newValue)+")");
        this.testValue = newValue;
        setChanged();
        notifyObservers();
    }
    /* Temporary function for testing through the GUI */
    public void incrementTestValue() {
        this.testValue++;
        setChanged();
        notifyObservers();
    }
}
