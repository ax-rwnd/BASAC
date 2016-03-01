package d0020e.basac;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;

/**
 * Created by Joppe on 2016-01-27.
 */
public class MotionSensor implements SensorEventListener{
    private static final String TAG ="MotionSensor";
    private Context mContext;

    public SensorManager sm;

    public MotionSensor(Context c) {
        this.mContext = c;

        sm = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        Sensor smAccel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, smAccel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
            // Do not trigger a warning if accelerometer is disabled in settings
            if (!pref.getBoolean("settings_accelerometer_enable", true)) {
                return;
            }
            double posX = event.values[0];
            double posY = event.values[1];
            double posZ = event.values[2];

            double speed = Math.sqrt(posX*posX+posY*posY+posZ*posZ);
            if (DataModel.getInstance().getValue(DataStore.VALUE_ACCELEROMETER) != 10) {
                DataModel.getInstance().setValue(DataStore.VALUE_ACCELEROMETER, 10);
            }
            if (speed < DataStore.THRESHOLD_ACCELEROMETER_LOW || speed > DataStore.THRESHOLD_ACCELEROMETER_HIGH) {
                DataModel.getInstance().setValue(DataStore.VALUE_ACCELEROMETER, speed);
                DataModel.getInstance().setUpdate();
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
