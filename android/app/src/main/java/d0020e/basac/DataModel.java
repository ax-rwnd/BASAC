package d0020e.basac;

import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;

/**
 * The Datamodel contains all sensordata and is updated when new data is recived from the vest.
 *
 */
public class DataModel extends Observable {
    private static final String TAG = "DataModel";

    private static DataModel ourInstance = new DataModel();
    private ArrayList<Double> dataValues = new ArrayList<>();

    public static DataModel getInstance() {
        return ourInstance;
    }

    private DataModel() {}

    public double getValue(int index) {
        return dataValues.get(index);
    }

    public int getSize() {
        return dataValues.size();
    }

    public void addValue(int index, double value) {
        dataValues.add(index, value);
    }

    public void setValue(int index, double value) {
        try {
            dataValues.set(index, value);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Index " + index + " is not set in DataModel");
            e.printStackTrace();
        }
    }
    public void setUpdate() {
        setChanged();
        notifyObservers();
    }
}
