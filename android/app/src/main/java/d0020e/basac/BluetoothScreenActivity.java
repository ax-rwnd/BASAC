package d0020e.basac;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BluetoothScreenActivity extends AppCompatActivity {

    private BroadcastReceiver mBluetoothDiscoveryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_screen);

        final ArrayAdapter<String> mDeviceArray = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        ListView mDeviceList = (ListView) findViewById(R.id.bt_devices);
        mDeviceList.setAdapter(mDeviceArray);

        mBluetoothDiscoveryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDeviceArray.add(device.getName() + "\n" + device.getAddress());
                }
            }
        };

        registerReceiver(mBluetoothDiscoveryReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBluetoothDiscoveryReceiver);
    }
}
