package d0020e.basac;

import android.util.Log;
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


    public DataModel() {
        testValue = 0;
        warning = false;
        Log.d(TAG, "DataModel constructor");
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

    public void toggleWarning() {
        warning = !warning;
    }

    public boolean getWarningState() {
        return warning;
    }

    /* Temporary function for testing through the GUI */
    public void incrementTestValue() {
        this.testValue++;
        if (this.testValue > 100) {
            testValue = 0;
        }
        Log.d(TAG, "incrementTestValue()");
        setChanged();
        notifyObservers();
    }

    public void notifyView() {
        Log.d(TAG, Integer.toString(this.testValue));
        setChanged();
        notifyObservers();
    }

}
