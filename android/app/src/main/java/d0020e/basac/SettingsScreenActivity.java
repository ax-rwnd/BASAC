package d0020e.basac;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class SettingsScreenActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was checked
        switch(view.getId()) {
            case R.id.checkbox_1:
                if (checked) {

                }
                else {

                }
                break;
            case R.id.checkbox_2:
                if (checked) {

                }
                else {

                }
                break;
        }
    }

    public void bluetooth_service(View view) {
        Intent intent = new Intent(this, BluetoothServiceScreenActivity.class);
        startActivity(intent);
    }

    public void bluetooth_client(View view) {
        Intent intent = new Intent(this, BluetoothClientScreenActivity.class);
        startActivity(intent);
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
            startActivity(intent);
        }
    }

    public void bt_discoverable(View view) {
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
            startActivity(discoverableIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == HomeScreenActivity.BLUETOOTH_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
