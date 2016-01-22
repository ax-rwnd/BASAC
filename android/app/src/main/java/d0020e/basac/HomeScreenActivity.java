package d0020e.basac;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class HomeScreenActivity extends AppCompatActivity {
    private static final String TAG = "HomeScreen";

    public static final int BLUETOOTH_REQUEST_CODE = 1;
    public static final int BLUETOOTH_RESULT_DEVICE = 2;
    private BroadcastReceiver mBluetoothReceiver;
    private boolean mBluetoothReceiverRegistered;

    private DataModel dataModel;
    private StateController stateController;
    private Button dataButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.e("HomeScreen", "Datamodel Created");
        /* Starts the StateController as a seperate thread*/
        /*new Thread(new Runnable() {
            public void run() {
                dataModel = new DataModel();
                stateController = new StateController(dataModel);
                // TODO: implement observer-observable pattern between stateController & Bluetooth manager.
            }
        }).start();*/

        dataModel = new DataModel();
        stateController = new StateController(dataModel);

        setContentView(R.layout.activity_home_screen);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        dataButton = (Button) findViewById(R.id.action_data);
        dataButton.setText("DATA");

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            mBluetoothReceiverRegistered = false;
        } else {
            mBluetoothReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        updateBluetoothStatus(state);
                    }
                }
            };
            this.registerReceiver(mBluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            mBluetoothReceiverRegistered = true;
            updateBluetoothStatus(mBluetoothAdapter.getState());
        }
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothReceiverRegistered) {
            this.unregisterReceiver(mBluetoothReceiver);
        }
    }

    private void updateBluetoothStatus(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_TURNING_OFF:
                //Toast.makeText(getApplicationContext(), "Bluetooth turning off", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_OFF:
                Toast.makeText(getApplicationContext(), "Bluetooth off", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                //Toast.makeText(getApplicationContext(), "Bluetooth turning on", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_ON:
                Toast.makeText(getApplicationContext(), "Bluetooth on", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_CONNECTED:

                break;
            case BluetoothAdapter.STATE_CONNECTING:

                break;
            case BluetoothAdapter.STATE_DISCONNECTED:

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsScreenActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_data) {
            Intent intent = new Intent(this, DataScreenActivity.class);
            intent.putExtra("dataModel", dataModel);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startDataScreen(View view) {
        Intent intent = new Intent(this, DataScreenActivity.class);
        intent.putExtra("dataModel", dataModel);
        startActivity(intent);
    }

    public void startSettingsScreen(View view) {
        Intent intent = new Intent(this, SettingsScreenActivity.class);
        intent.putExtra("dataModel", dataModel);
        startActivity(intent);
    }

    public void onResume() {
        super.onResume();

    }

}
