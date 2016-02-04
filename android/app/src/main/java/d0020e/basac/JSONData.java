package d0020e.basac;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;

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
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            Log.e(TAG, "Bluetooth not available");
            return;
        }
        finalJSON = new JSONObject();
        try {
            //finalJSON.put("MAC", BluetoothAdapter.getDefaultAdapter().getAddress());
            finalJSON.put("timestamp", new Date().getTime());
            finalJSON.put("values" , new JSONObject());
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
            finalJSON.put("timestamp", new Date().getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "JSONData: " + finalJSON.toString());
    }

    public JSONObject getJSON() {
        try {
            finalJSON.put("timestamp", new Date().getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return finalJSON;
    }

}
