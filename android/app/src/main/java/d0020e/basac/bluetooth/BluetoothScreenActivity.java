package d0020e.basac.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import d0020e.basac.R;

public class BluetoothScreenActivity extends AppCompatActivity {

    private final BroadcastReceiver mBluetoothDiscoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                TextView bt_device = (TextView) findViewById(R.id.bt_device);
                bt_device.setText(R.string.bt_scan);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                TextView bt_device = (TextView) findViewById(R.id.bt_device);
                bt_device.setText(R.string.bt_scan_finished);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceArray.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };

    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mDeviceArray;
    private ArrayAdapter<String> mPairedDeviceArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_screen);
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth not available", Toast.LENGTH_SHORT).show();
            return;
        }

        mDeviceArray = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1);
        ListView mDeviceList = (ListView) findViewById(R.id.bt_devices);
        mDeviceList.setAdapter(mDeviceArray);
        mPairedDeviceArray = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBluetoothDiscoveryReceiver, filter);

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        ListView mPairedDeviceList = (ListView) findViewById(R.id.bt_paired_devices);
        if (pairedDevices.size() > 0) {
            mPairedDeviceList.setAdapter(mPairedDeviceArray);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDeviceArray.add(device.getName() + "\n" + device.getAddress());
            }
        }

        mDeviceList.setOnItemClickListener(mDeviceOnClickListener);
        mPairedDeviceList.setOnItemClickListener(mDeviceOnClickListener);

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

    private OnItemClickListener mDeviceOnClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            // Cancel discovery because it's costly and we're about to connect
            mBluetoothAdapter.cancelDiscovery();
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra("device_address", address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    public void bt_rescan(View view) {
        mDeviceArray.clear();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(mBluetoothDiscoveryReceiver);
    }
}
