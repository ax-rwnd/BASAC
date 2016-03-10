package d0020e.basac;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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

/**
 * The container class for sensordata.
 */
    //TODO: comment.
public class JSONData {
    private static final String TAG = "JSONData";

    private JSONObject json;

    public JSONData() {
        json = new JSONObject();
        try {
            if (BluetoothAdapter.getDefaultAdapter() != null) {
                json.put("MAC", BluetoothAdapter.getDefaultAdapter().getAddress());
            }
            json.put("timestamp", System.currentTimeMillis());
            json.put("data", new JSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void putData(String name, Object value) {
        try {
            json.getJSONObject("data").put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "putData() failed to put " + name + ": " + value.toString());
        }
    }

    public void removeData(String name) {
        try {
            json.getJSONObject("data").remove(name);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "removeData() failed to remove value: values." + name);
        }
    }

    public Object getDataValue(String name) {
        try {
            return json.getJSONObject("data").get(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object get(String name) {
        try {
            return json.get(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void logJSON() {
        try {
            json.put("timestamp", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, json.toString());
    }
    public void updateTimestamp() {
        try {
            json.put("timestamp", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJSON() {
        return json;
    }

    public String toString() {
        return this.json.toString();
    }

}
