package d0020e.basac;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.Observable;

/**
 * The Datamodel, holds the data recieved from the "Väst"
 */
public class DataModel extends Observable implements Serializable {
    private static int testValue;

    public DataModel() {
        this.testValue = 0;
        Log.e("DataModel", "Datamodel constructor");
    }

    public int getTestValue() {
        return testValue;
    }
    /* Sets value to the actual value, invoked by StateController */
    public void setTestValue(int newValue) {
        this.testValue = newValue;
        notifyObservers();
    }
    /* Temporary function for testing through the GUI */
    public void incrementTestValue() {
        this.testValue++;
        setChanged();
        notifyObservers();
    }
}
