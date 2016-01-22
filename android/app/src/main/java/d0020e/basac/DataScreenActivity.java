package d0020e.basac;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    public static int isRunning = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);

        Bundle data = getIntent().getExtras();
        dataModel = (DataModel) data.getSerializable("dataModel");
        dataModel.addObserver(this);
        System.out.println("Datamodel: " + dataModel.toString());
        System.out.println(dataModel.countObservers());

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


    public void updateProgressbar() {
        oxygenProgress.setProgress(dataModel.getTestValue());
    }

    public void incrementProgressbar() {
        dataModel.incrementTestValue();
    }

    public void update(Observable observable, Object data) {
        Log.d("DataScreen", "update()");
        updateProgressbar();
    }
    public void onStop() {
        super.onStop();
        this.isRunning = 2;
    }

    public void onStart() {
        super.onStart();
        this.isRunning = 1;
    }
}
