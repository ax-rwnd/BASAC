package d0020e.basac.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;

import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;
import d0020e.basac.DataModel;
import d0020e.basac.DataStore;

/**
 * Created by WeeDzCokie on 2016-03-01.
 */
public class BluetoothArduino {
    private static final String TAG = "BluetoothArduino";

    private ArduinoReceiver arduinoReceiver;

    private Context mContext;
    private String device_address;

    String[] fields = null;
    String data = null;

    public BluetoothArduino(Context c, String address) {
        Log.d(TAG, "Constructor()");
        mContext = c;
        device_address = address;

        // in order to receive broadcasted intents we need to register our receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AmarinoIntent.ACTION_RECEIVED);
        intentFilter.addAction(AmarinoIntent.ACTION_CONNECT);
        intentFilter.addAction(AmarinoIntent.ACTION_CONNECTED);
        intentFilter.addAction(AmarinoIntent.ACTION_CONNECTION_FAILED);
        intentFilter.addAction(AmarinoIntent.ACTION_PAIRING_REQUESTED);
        intentFilter.addAction(AmarinoIntent.ACTION_DISCONNECTED);

        arduinoReceiver = new ArduinoReceiver();
        mContext.registerReceiver(arduinoReceiver, intentFilter);
        // this is how you tell Amarino to connect to a specific BT device from within your own code
        Amarino.connect(mContext, device_address);
    }

    public void stop() {
        Log.d(TAG, "stop()");
        // if you connect in onStart() you must not forget to disconnect when your app is closed
        Amarino.disconnect(mContext, device_address);

        // do never forget to unregister a registered receiver
        mContext.unregisterReceiver(arduinoReceiver);
    }

    public void turnonvibe() {
        int om = 220;
        Amarino.sendDataToArduino(mContext, device_address, 'A', om);
    }


    /**
     * ArduinoReceiver is responsible for catching broadcasted Amarino
     * events.
     *
     * It extracts data from the intent.
     */
    public class ArduinoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (AmarinoIntent.ACTION_CONNECTED.equals(intent.getAction())) {
                Log.d(TAG, "Connected");
            }
            if (AmarinoIntent.ACTION_CONNECT.equals(intent.getAction())) {
                Log.d(TAG, "Connect");
            }
            if (AmarinoIntent.ACTION_DISCONNECTED.equals(intent.getAction())) {
                Log.d(TAG, "Disconnected");
            }
            if (AmarinoIntent.ACTION_CONNECTION_FAILED.equals(intent.getAction())) {
                Log.d(TAG, "Connection failed");
            }
            if (AmarinoIntent.ACTION_PAIRING_REQUESTED.equals(intent.getAction())) {
                Log.d(TAG, "Pairing requested");
            }
            if (AmarinoIntent.ACTION_RECEIVED.equals(intent.getAction())) {
                // the type of data which is added to the intent
                final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
                //Log.d(TAG, "DataType: " + dataType);
                if (dataType == AmarinoIntent.STRING_EXTRA) {
                    data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
                    Log.d(TAG, "Data: " + data);

                    if (data != null) {
                        String patternStr = ","; //separator value
                        fields = data.split(patternStr);//array where values will be stored.
                        //s = fields;   // Copy of string
                        char c = fields[0].charAt(0);

                        // check if the data is in correct format
                        if ((c == '@')) {
                            // Temperature in
                            if (fields[1] != null && !fields[1].isEmpty() && !fields[1].trim().isEmpty()) {
                                float f = Float.valueOf(fields[1].trim());

                                // ------ Set value in DataModel ------
                                DataModel.getInstance().setValue(DataStore.VALUE_SKIN_TEMPERATURE, f);
                            }

                            // Temperature out
                            if (fields[2] != null && !fields[2].isEmpty() && !fields[2].trim().isEmpty()) {
                                float f = Float.valueOf(fields[2].trim());

                                // ------ Set value in DataModel ------
                                DataModel.getInstance().setValue(DataStore.VALUE_ENV_TEMPERATURE, f);
                            }

                            // Humidity
                            if (fields[3] != null && !fields[3].isEmpty() && !fields[3].trim().isEmpty()) {
                                float f = Float.valueOf(fields[3].trim());

                                // ------ Set value in DataModel ------
                                DataModel.getInstance().setValue(DataStore.VALUE_HUMIDITY, f);
                            }

                            // Heart beat
                            if (fields[4] != null && !fields[4].isEmpty() && !fields[4].trim().isEmpty()) {
                                int heart_beat = Integer.parseInt(fields[4]);

                                // ------ Set value in DataModel ------
                                DataModel.getInstance().setValue(DataStore.VALUE_HEARTRATE, heart_beat);
                            }

                            // CO gas
                            if (fields[5] != null && !fields[5].isEmpty() && !fields[5].trim().isEmpty()) {
                                float f = Float.valueOf(fields[5].trim());

                                // ------ Set value in DataModel ------
                                DataModel.getInstance().setValue(DataStore.VALUE_CO, f);
                            }

                            if (fields[6] != null && !fields[6].isEmpty() && !fields[6].trim().isEmpty()) {
                                int n = Integer.parseInt(fields[6]);
                                //Log.d(TAG, "unknown value, fields[7]: " + fields[6]);
                            }

                            if (fields[7] != null && !fields[7].isEmpty() && !fields[7].trim().isEmpty()) {
                                float f = Float.valueOf(fields[7].trim());
                                //Log.d(TAG, "unknown value, fields[7]: " + fields[7]);
                            }
                        }
                    }
                }
                DataModel.getInstance().setUpdate();
            }
        }

    }

}
