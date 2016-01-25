package d0020e.basac;

import android.graphics.PorterDuff;
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
    private DataModel dataModel;
    private Button dataButton;

    public int isRunning = 0;

    private TempService updateGUI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);

        Bundle data = getIntent().getExtras();
        dataModel = (DataModel) data.getSerializable("dataModel");
        dataModel.addObserver(this);
        if (updateGUI == null) {
            updateGUI = new TempService(dataModel);
            updateGUI.start();
        }

        oxygenProgress = (ProgressBar) findViewById(R.id.oxygen_bar);
        oxygenProgress.setProgress(dataModel.getTestValue());

        dataButton = (Button) findViewById(R.id.action_progress);
        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementProgressbar();
            }
        });

        this.isRunning = 1;

    }

    public void onPause() {
        super.onPause();
        updateGUI.interrupt();
    }

    public void onResume() {
        super.onResume();
        if (updateGUI != null && updateGUI.isInterrupted()) {
            updateGUI.start();
        }
    }

    public void updateProgressbar() {
        oxygenProgress.setProgress(dataModel.getTestValue());
    }

    public void incrementProgressbar() {
        dataModel.incrementTestValue();
    }

    public void update(Observable observable, Object data) {
        this.updateProgressbar();
        if(dataModel.getWarningState()) {
            dataButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        }
        Log.d("DataScreen", "Update");
    }

    public void onStop() {
        super.onStop();
        this.isRunning = 2;
        if (updateGUI != null) {
            updateGUI.interrupt();
        }
    }

    public void onStart() {
        super.onStart();
        this.isRunning = 1;
        if (updateGUI != null && updateGUI.isInterrupted()) {
            updateGUI.start();
        }
    }
}
