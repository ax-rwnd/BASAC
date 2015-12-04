package d0020e.basac;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

public class DataScreenActivity extends AppCompatActivity {
    private ProgressBar oxygenProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        oxygenProgress = (ProgressBar) findViewById(R.id.oxygen_bar);

    }


    public void updateProgressbar(View view) {
        oxygenProgress.incrementProgressBy(1);
       // oxygenProgress.setProgress(50);
    }

}
