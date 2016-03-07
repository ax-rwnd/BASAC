package d0020e.basac;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

/**
 * TODO: Show threshold values
 */
public class DataScreenActivity extends AppCompatActivity implements Observer {

    public int isRunning = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);

        update();

        Button dataButton = (Button) findViewById(R.id.update_data);
        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        this.isRunning = 1;
    }

    private void setLastUpdate() {
        DataStore ds = (DataStore)getApplication();
        Date date = new Date(ds.mState.getLastUpdate());
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.getDefault());
        TextView last_update = (TextView)findViewById(R.id.last_update);
        last_update.setText(String.format("Last update %s", format.format(date)));
    }

    public void updateOxygenBar() {
        ProgressBar bar = (ProgressBar) findViewById(R.id.oxygen_bar);
        bar.setProgress((int)DataModel.getInstance().getValue(DataStore.VALUE_OXYGEN));
        TextView text = (TextView) findViewById(R.id.oxygen_value);
        text.setText(String.format(Locale.getDefault(), "%.2f%%", DataModel.getInstance().getValue(DataStore.VALUE_OXYGEN)));
    }
    public void updateEnvTemperatureBar() {
        ProgressBar bar = (ProgressBar) findViewById(R.id.env_temperature_bar);
        bar.setProgress((int)DataModel.getInstance().getValue(DataStore.VALUE_ENV_TEMPERATURE));
        TextView text = (TextView) findViewById(R.id.env_temperature_value);
        text.setText(String.format(Locale.getDefault(), "%.1f C", DataModel.getInstance().getValue(DataStore.VALUE_ENV_TEMPERATURE)));
    }
    public void updateSkinTemperatureBar() {
        ProgressBar bar = (ProgressBar) findViewById(R.id.skin_temperature_bar);
        bar.setProgress((int)DataModel.getInstance().getValue(DataStore.VALUE_SKIN_TEMPERATURE));
        TextView text = (TextView) findViewById(R.id.skin_temperature_value);
        text.setText(String.format(Locale.getDefault(), "%.1f C" ,DataModel.getInstance().getValue(DataStore.VALUE_SKIN_TEMPERATURE)));
    }
    public void updateHeartRateBar() {
        ProgressBar bar = (ProgressBar) findViewById(R.id.heartrate_bar);
        bar.setProgress((int)DataModel.getInstance().getValue(DataStore.VALUE_HEARTRATE));
        TextView text = (TextView) findViewById(R.id.heartrate_value);
        text.setText(String.format(Locale.getDefault(), "%.0f bpm", DataModel.getInstance().getValue(DataStore.VALUE_HEARTRATE)));
    }
    public void updateAirPressureBar() {
        ProgressBar bar = (ProgressBar) findViewById(R.id.airpressure_bar);
        bar.setProgress((int)DataModel.getInstance().getValue(DataStore.VALUE_AIRPRESSURE));
        TextView text = (TextView) findViewById(R.id.airpressure_value);
        text.setText(String.format(Locale.getDefault(), "%.0f Pa ", DataModel.getInstance().getValue(DataStore.VALUE_AIRPRESSURE)));
    }
    public void updateHumidityBar() {
        ProgressBar bar = (ProgressBar) findViewById(R.id.humidity_bar);
        bar.setProgress((int)DataModel.getInstance().getValue(DataStore.VALUE_HUMIDITY));
        TextView text = (TextView) findViewById(R.id.humidity_value);
        text.setText(String.format(Locale.getDefault(), "%.1f %%", DataModel.getInstance().getValue(DataStore.VALUE_HUMIDITY)));
    }
    public void updateCoBar() {
        ProgressBar bar = (ProgressBar) findViewById(R.id.co_bar);
        bar.setProgress((int)DataModel.getInstance().getValue(DataStore.VALUE_CO));
        TextView text = (TextView) findViewById(R.id.co_value);
        text.setText(String.format(Locale.getDefault(), "%.0f PPM", DataModel.getInstance().getValue(DataStore.VALUE_CO)));
    }

    public void update(Observable observable, Object data) {
        this.update();
    }

    public void update() {
        updateOxygenBar();
        updateEnvTemperatureBar();
        updateSkinTemperatureBar();
        updateHeartRateBar();
        updateAirPressureBar();
        updateHumidityBar();
        updateCoBar();
        setLastUpdate();
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
