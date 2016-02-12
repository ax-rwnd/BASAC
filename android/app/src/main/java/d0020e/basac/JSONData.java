package d0020e.basac;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by weedz on 2016-02-03.
 */

/*

JSON structure:

{
    "MAC": "02:00:00:00:00:00",
    "timestamp": 1454496535538,
    "data": {
        "value_01": "value",
        "value_02": "value 02"
    }
}

 */
public class JSONData {
    private static final String TAG = "JSONData";

    private JSONObject finalJSON;

    public JSONData() {
        finalJSON = new JSONObject();
        try {
            if (BluetoothAdapter.getDefaultAdapter() != null) {
                finalJSON.put("MAC", BluetoothAdapter.getDefaultAdapter().getAddress());
            }
            finalJSON.put("timestamp", System.currentTimeMillis());
            finalJSON.put("values", new JSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void put(String name, Object value) {
        try {
            finalJSON.getJSONObject("values").put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "put() failed to put " + name + ": " + value.toString());
        }
    }

    public void remove(String name) {
        try {
            finalJSON.getJSONObject("values").remove(name);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "remove() failed to remove value: values." + name);
        }
    }

    public Object getDataValues(String name) {
        try {
            return finalJSON.getJSONObject("values").get(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object get(String name) {
        try {
            return finalJSON.get(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void logJSON() {
        try {
            finalJSON.put("timestamp", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "JSONData: " + finalJSON.toString());
    }

    public JSONObject getJSON() {
        try {
            finalJSON.put("timestamp", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return finalJSON;
    }

    public String toString() {
        return this.finalJSON.toString();
    }

}
