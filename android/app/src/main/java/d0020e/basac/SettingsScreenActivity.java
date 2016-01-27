package d0020e.basac;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsScreenActivity extends AppCompatActivity {

    private static final String TAG = "SettingsScreen";

    private BluetoothClient mBluetoothClient;

    private BluetoothAdapter mBluetoothAdapter;
    private String mDeviceAddress;
    private DataModel mDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Bundle data = getIntent().getExtras();
        mDataModel = (DataModel) data.getSerializable("dataModel");

        Button mBTClient = (Button) findViewById(R.id.bluetooth_client);
        mBTClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeviceAddress != null) {
                    mBluetoothClient = new BluetoothClient(mDeviceAddress, mDataModel);
                }
            }
        });
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_1);
        if (!checkBox.isChecked()) {
            checkBox.setChecked(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothAdapter != null) {
            TextView mPairedDevice = (TextView) findViewById(R.id.bt_paired_device);
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was checked
        switch(view.getId()) {
            case R.id.checkbox_1:
                if (checked) {
                    Log.i("box 1 :", "checked");
                }
                else {
                    Log.i("box 1 :", "NOT checked");
                }
                break;
            case R.id.checkbox_2:
                if (checked) {
                    Log.i("box 2 :", "checked");
                }
                else {
                    Log.i("box 2 :", "NOT .checked");
                }
                break;
        }
    }

    public void bluetooth_server(View view) {
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth not available", Toast.LENGTH_SHORT).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Make sure bluetooth is turned on before starting server", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, BluetoothServerScreenActivity.class);
            intent.putExtra("dataModel", mDataModel);
            startActivity(intent);
        }
    }

    public void bluetooth_client(View view) {
        if (mDeviceAddress != null) {
            mBluetoothClient = new BluetoothClient(mDeviceAddress, mDataModel);
        } else {
            Toast.makeText(getApplicationContext(),"Connect to a device",Toast.LENGTH_SHORT).show();
        }
    }

    public void toggle_bluetooth(View view) {
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth not available", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "Turning on bluetooth", Toast.LENGTH_SHORT).show();
                mBluetoothAdapter.enable();
            } else {
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
            startActivityForResult(intent, HomeScreenActivity.BLUETOOTH_RESULT_DEVICE);
        }
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
        mDataModel.setTestValue(100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case HomeScreenActivity.BLUETOOTH_REQUEST_CODE:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
                }
                break;
            case HomeScreenActivity.BLUETOOTH_RESULT_DEVICE:
                mDeviceAddress = data.getStringExtra("device_address");
                TextView pairedDevice = (TextView) findViewById(R.id.bt_paired_device);
                pairedDevice.setText(mDeviceAddress);
                break;
        }
    }

}
