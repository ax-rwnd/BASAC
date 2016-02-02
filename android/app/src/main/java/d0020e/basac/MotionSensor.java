package d0020e.basac;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Observable;

/**
 * Created by Joppe on 2016-01-27.
 */
public class MotionSensor extends Observable implements SensorEventListener{
    private final String TAG ="Motion Sensor";
    private SensorManager sm;
    private Sensor smAccel;
    private long lastEvent=0;
    private float previous_x, previous_y, previous_z;
    private static final int movment_threshold = 600;

    public MotionSensor(){
        Log.d(TAG, "Contructor");

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if(sensor.getType()== Sensor.TYPE_ACCELEROMETER){
            float posX = event.values[0];
            float posY = event.values[1];
            float posZ = event.values[2];

            String position = "x: " + Float.toString(posX) + " y: " + Float.toString(posY) + " y: " + Float.toString(posZ);
            /*long currentTime = System.currentTimeMillis();

            if ((currentTime-lastEvent) > 100 ) {
                long deltaTime = (currentTime - lastEvent);
                lastEvent = currentTime;
            } */
            Log.d("Motion Sensor", position);
        }




        setChanged();
        notifyObservers();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
