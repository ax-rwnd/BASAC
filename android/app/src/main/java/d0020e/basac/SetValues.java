package d0020e.basac;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;

public class SetValues extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_values);
    }

    public void set_values(View view) {
        int value;
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        SeekBar seekBar = (SeekBar) findViewById(R.id.oxygen_seekBar);
        DataModel.getInstance().setValue(DataStore.VALUE_OXYGEN, seekBar.getProgress());
        editor.putInt("data_"+DataStore.VALUE_OXYGEN, seekBar.getProgress());

        seekBar = (SeekBar) findViewById(R.id.temperature_seekBar);
        DataModel.getInstance().setValue(DataStore.VALUE_TEMPERATURE, seekBar.getProgress());
        editor.putInt("data_"+DataStore.VALUE_TEMPERATURE, seekBar.getProgress());

        seekBar = (SeekBar) findViewById(R.id.heartrate_seekBar);
        DataModel.getInstance().setValue(DataStore.VALUE_HEARTRATE, seekBar.getProgress());
        editor.putInt("data_"+DataStore.VALUE_HEARTRATE, seekBar.getProgress());

        seekBar = (SeekBar) findViewById(R.id.airpressure_seekBar);
        value = 300000 * seekBar.getProgress()/100;
        DataModel.getInstance().setValue(DataStore.VALUE_AIRPRESSURE, value);
        editor.putInt("data_"+DataStore.VALUE_AIRPRESSURE, value);

        seekBar = (SeekBar) findViewById(R.id.humidity_seekBar);
        DataModel.getInstance().setValue(DataStore.VALUE_HUMIDITY, seekBar.getProgress());
        editor.putInt("data_"+DataStore.VALUE_HUMIDITY, seekBar.getProgress());

        seekBar = (SeekBar) findViewById(R.id.co_seekBar);
        value = 200 * seekBar.getProgress() / 100;
        DataModel.getInstance().setValue(DataStore.VALUE_CO, value);
        editor.putInt("data_"+DataStore.VALUE_CO, value);

        editor.apply();
        DataModel.getInstance().setUpdate();
    }
}
