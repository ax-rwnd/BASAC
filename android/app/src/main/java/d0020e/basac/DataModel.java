package d0020e.basac;

import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by WeeDzCokie on 2016-01-28.
 */
public class DataModel extends Observable {
    private static final String TAG = "DataModel";

    private static DataModel ourInstance = new DataModel();
    private ArrayList<Integer> dataValues = new ArrayList<>();

    public static DataModel getInstance() {
        return ourInstance;
    }

    private DataModel() {
        dataValues = new ArrayList<>();
    }

    public int getValue(int index) {
        return dataValues.get(index);
    }

    public void addValue(int value) {
        dataValues.add(value);
    }

    public void setValue(int index, int value) {
        try {
            dataValues.set(index, value);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Index " + index + " is not set in DataModel");
            e.printStackTrace();
        }
        setChanged();
        notifyObservers();
    }
}
