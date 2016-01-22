package d0020e.basac;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

/**
 * The Datamodel, holds the data recieved from the "Väst"
 */
public class DataModel extends Observable implements Serializable {
    private static final String TAG = "DataModel";

    private static int testValue;

    public DataModel() {
        this.testValue = 0;
        Log.d(TAG, "constructor");
        System.out.println("Datamodel: " + this.toString());
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
        System.out.println("Datamodel: " + this.toString());
        System.out.println(countObservers());
        setChanged();
        notifyObservers();
    }

    public void incrementTestValue(int value) {
        this.testValue += value;
        setChanged();
        notifyObservers();
    }
}
