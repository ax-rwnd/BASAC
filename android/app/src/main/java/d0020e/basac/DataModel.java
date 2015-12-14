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
    private static boolean warning;

    public DataModel() {
        testValue = 0;
        warning = false;
        Log.e("DataModel", "Datamodel constructor");
    }

    public int getTestValue() {
        return testValue;
    }
    /* Sets value to the actual value, invoked by StateController */
    public void setTestValue(int newValue) {
        testValue = newValue;
        notifyObservers();
    }

    public void toggleWarning() {
        warning = !warning;
    }

    public boolean getWarningState() {
        return warning;
    }
    /* Temporary function for testing through the GUI */
    public void incrementTestValue() {
        testValue++;
        Log.e("incrementButton: ", Integer.toString(this.countObservers()));
        this.countObservers();
        setChanged();
        notifyObservers();
    }
}
