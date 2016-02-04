package d0020e.basac;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsScreenActivity extends AppCompatActivity {

    private static final String TAG = "SettingsScreen";

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void bluetooth_server(View view) {
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth not available", Toast.LENGTH_SHORT).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Make sure bluetooth is turned on before starting server", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, BluetoothServerScreenActivity.class);
            startActivity(intent);
        }
    }

    public void toggle_bluetooth(View view) {
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(this, "Turning on bluetooth", Toast.LENGTH_SHORT).show();
                mBluetoothAdapter.enable();
            } else {
                Toast.makeText(this, "Turning off bluetooth", Toast.LENGTH_SHORT).show();
                mBluetoothAdapter.disable();
            }
        }
    }

    public void bluetooth_connect(View view) {
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth not available", Toast.LENGTH_SHORT).show();
        } else if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            Toast.makeText(getApplicationContext(), "Turn on bluetooth", Toast.LENGTH_SHORT).show();
        } else if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            Intent intent = new Intent(this, BluetoothScreenActivity.class);
            startActivityForResult(intent, DataStore.BLUETOOTH_RESULT_DEVICE);
        }
    }

    public void bluetooth_disconnect(View view) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean("start_bluetooth", false);
        editor.apply();
        DataStore ds = (DataStore)getApplication();
        ds.mState.stopBluetoothConnection();
    }

    public void bt_discoverable(View view) {
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
            startActivity(discoverableIntent);
        }
    }

    public void temp_func(View view) {
        Log.d(TAG,"Set test value = 100");
        Toast.makeText(getApplicationContext(),"Set test value = 100", Toast.LENGTH_SHORT).show();
        DataModel.getInstance().setValue(0,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DataStore.BLUETOOTH_REQUEST_CODE:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
                }
                break;
            case DataStore.BLUETOOTH_RESULT_DEVICE:
                if (data != null) {
                    String mDeviceAddress = data.getStringExtra("device_address");
                    TextView pairedDevice = (TextView) findViewById(R.id.bt_paired_device);
                    pairedDevice.setText(mDeviceAddress);

                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    editor.putString("device_address", mDeviceAddress);
                    editor.putBoolean("start_bluetooth", true);
                    editor.apply();

                    DataStore ds = (DataStore)getApplication();
                    ds.mState.startBluetoothConnection();
                }
                break;
        }
    }

}
