package d0020e.basac;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.Observable;
import java.util.Observer;

public class DataScreenActivity extends AppCompatActivity implements Observer {
    private ProgressBar oxygenProgress;

    public int isRunning = 0;
    private DataStore ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);

        oxygenProgress = (ProgressBar) findViewById(R.id.oxygen_bar);
        oxygenProgress.setProgress((int) Math.round(DataModel.getInstance().getValue(0)));

        ds = (DataStore)getApplicationContext();
        ds.mState.setContext(this);

        Button dataButton = (Button) findViewById(R.id.action_progress);
        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementProgressbar();
            }
        });

        this.isRunning = 1;
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

    public void onResume() {
        super.onResume();
        ds.mState.setContext(this);

    }

}
