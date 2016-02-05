package d0020e.basac;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class DataScreenActivity extends AppCompatActivity implements Observer {
    private ProgressBar oxygenProgress,accelBar;
    private SeekBar accelSeekBar;
    private TextView oxygenValue, oxygenThreshold,accelValue,accelThreshold;

    public int isRunning = 0;

    @Override
    //Todo: Change so threshold value is fetched from some fancy place
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);


       /* accelSeekBar = (SeekBar) findViewById(R.id.accel_seekBar);
        accelSeekBar.setMax(50);
        accelSeekBar.setProgress((int) Math.round(DataModel.getInstance().getValue(1)));

        accelValue = (TextView) findViewById(R.id.accel_value);
        accelBar = (ProgressBar) findViewById(R.id.accelerometer_bar);
        accelBar.setProgress((int)Math.round(DataModel.getInstance().getValue(1)));
        accelThreshold = (TextView) findViewById(R.id.accel_threshold);
        accelThreshold.setText("Threshold: " + 2);
        oxygenValue = (TextView) findViewById(R.id.current_value);
        oxygenThreshold = (TextView) findViewById(R.id.oxygen_threshold);
        oxygenThreshold.setText("Threshold: " + 30); */
        oxygenProgress = (ProgressBar) findViewById(R.id.oxygen_bar);
        oxygenProgress.setProgress((int) Math.round(DataModel.getInstance().getValue(0)));

        this.setLastUpdate();

        Button dataButton = (Button) findViewById(R.id.action_progress);
        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementProgressbar();
            }
        });

        this.isRunning = 1;
    }

    private void setLastUpdate() {
        DataStore ds = (DataStore)getApplication();
        Date date = new Date(ds.mState.getLastUpdate());
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        TextView last_update = (TextView)findViewById(R.id.last_update);
        last_update.setText("Last update: " + format.format(date));
    }

    public void updateProgressbar() {
        oxygenProgress.setProgress((int)Math.round(DataModel.getInstance().getValue(0)));
    }

    public void updateCurrentOxygenValues(){
        oxygenValue.setText("Current value: " + DataModel.getInstance().getValue(0));
    }
    public void updateAccelValues(){
        accelValue.setText("Current value: " + DataModel.getInstance().getValue(1));
    }
    public void updateAccelBarValues(){
        accelBar.setProgress((int)Math.round(DataModel.getInstance().getValue(1)));
        accelSeekBar.setProgress((int)Math.round(DataModel.getInstance().getValue(1)));
    }



    public void incrementProgressbar() {
        DataModel.getInstance().setValue(0, DataModel.getInstance().getValue(0) + 5);
    }

    public void update(Observable observable, Object data) {
        this.update();
    }

    public void update() {
        this.updateProgressbar();
        //this.updateCurrentOxygenValues();
        //this.updateAccelBarValues();
        //this.updateAccelValues();
        this.setLastUpdate();
    }

    public void onStart() {
        super.onStart();
        DataModel.getInstance().addObserver(this);
        this.isRunning = 1;
    }

    public void onStop() {
        super.onStop();
        DataModel.getInstance().deleteObserver(this);
        this.isRunning = 2;
    }

}
