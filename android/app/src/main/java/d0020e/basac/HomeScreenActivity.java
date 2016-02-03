package d0020e.basac;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import android.hardware.Sensor;
import android.hardware.SensorManager;


public class HomeScreenActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "HomeScreen";

    private BroadcastReceiver mBluetoothReceiver;
    private boolean mBluetoothReceiverRegistered;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DataStore ds = (DataStore)getApplicationContext();
        ds.mState.setContext(this);

        /*Initialize accelerometer
         */
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor smAccel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, smAccel, SensorManager.SENSOR_DELAY_NORMAL);

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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
                //Toast.makeText(getApplicationContext(), "Bluetooth off", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                //Toast.makeText(getApplicationContext(), "Bluetooth turning on", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_ON:
                //Toast.makeText(getApplicationContext(), "Bluetooth on", Toast.LENGTH_SHORT).show();
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
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startDataScreen(View view) {
        Intent intent = new Intent(this, DataScreenActivity.class);
        startActivity(intent);
    }

    public void startSettingsScreen(View view) {
        Intent intent = new Intent(this, SettingsScreenActivity.class);
        startActivity(intent);
    }

    public void onResume() {
        super.onResume();

    }
    private long lastEvent;
    private float prevX,prevY,prevZ;
    private static final int threshold = 1000;
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            double posX = event.values[0];
            double posY = event.values[1];
            double posZ = event.values[2];

           // String position = "x: " + Double.toString(posX) + " y: " + Float.toString(posY) + " y: " + Float.toString(posZ);
            long currentTime = System.currentTimeMillis();

            //if ((currentTime-lastEvent) > 100 ) {
                long deltaTime = (currentTime - lastEvent);
                lastEvent = currentTime;
                double speed = Math.sqrt(posX*posX+posY*posY+posZ*posZ);
                //if (speed > threshold){
                    Log.d("Motion Sensor", Double.toString(speed));
                    DataModel.getInstance().setValue(1, speed);
                    //Log.d("Motion Sensor", Double.toString(speed));
                //}

            //}

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
