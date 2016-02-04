package d0020e.basac;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class DataScreenActivity extends AppCompatActivity implements Observer {
    private ProgressBar oxygenProgress;

    public int isRunning = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);

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

    public void incrementProgressbar() {
        DataModel.getInstance().setValue(0, DataModel.getInstance().getValue(0) + 5);
    }

    public void update(Observable observable, Object data) {
        this.update();
    }

    public void update() {
        this.updateProgressbar();
        this.setLastUpdate();

        /*if(mService.mModel.getWarningState()) {
            dataButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        }*/
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
