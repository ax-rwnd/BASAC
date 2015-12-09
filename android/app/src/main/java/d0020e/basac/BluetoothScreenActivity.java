package d0020e.basac;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

public class BluetoothScreenActivity extends AppCompatActivity {

    private final BroadcastReceiver mBluetoothDiscoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                TextView bt_device = (TextView) findViewById(R.id.bt_device);
                bt_device.setText("Scanning for devices...");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                TextView bt_device = (TextView) findViewById(R.id.bt_device);
                bt_device.setText("Finished scanning for devices");
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
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(getApplicationContext().BLUETOOTH_SERVICE);

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth not available", Toast.LENGTH_SHORT).show();
            return;
        }

        mDeviceArray = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        ListView mDeviceList = (ListView) findViewById(R.id.bt_devices);
        mDeviceList.setAdapter(mDeviceArray);
        mPairedDeviceArray = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);

        Toast.makeText(getApplicationContext(), "Started device discovery", Toast.LENGTH_SHORT).show();



        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBluetoothDiscoveryReceiver, filter);

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            ListView mPairedDeviceList = (ListView) findViewById(R.id.bt_paired_devices);
            mPairedDeviceList.setAdapter(mPairedDeviceArray);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDeviceArray.add(device.getName() + "\n" + device.getAddress());
            }
        }

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

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
