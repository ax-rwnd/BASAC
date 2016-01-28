package d0020e.basac;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.Observable;
import java.util.Observer;

public class DataScreenActivity extends AppCompatActivity implements Observer {
    private ProgressBar oxygenProgress;
    private Button dataButton;

    public int isRunning = 0;

    private UpdateGUIService updateGUI = null;

    private Boolean mBound = false;
    private DataMonitor mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DataMonitor.LocalBinder binder = (DataMonitor.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private class UpdateGUIService extends Thread {
        public void run() {
            while(true) {
                try {
                    update();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);

        if (updateGUI == null) {
            updateGUI = new UpdateGUIService();
            updateGUI.start();
        }

        oxygenProgress = (ProgressBar) findViewById(R.id.oxygen_bar);
        oxygenProgress.setProgress(DataModel.getInstance().getValue(0));

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
        oxygenProgress.setProgress(DataModel.getInstance().getValue(0));
    }

    public void incrementProgressbar() {
        DataModel.getInstance().setValue(0, DataModel.getInstance().getValue(0)+5);
    }

    public void update(Observable observable, Object data) {
        this.update();
    }

    public void update() {
        Log.d("DataScreen", "Update");
        this.updateProgressbar();
        /*if(mService.mModel.getWarningState()) {
            dataButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        }*/
    }

    public void onStart() {
        super.onStart();
        DataModel.getInstance().addObserver(this);
        this.isRunning = 1;
        if (updateGUI != null && updateGUI.isInterrupted()) {
            updateGUI.start();
        }
    }

    public void onStop() {
        super.onStop();
        DataModel.getInstance().deleteObserver(this);
        this.isRunning = 2;
        if (updateGUI != null) {
            updateGUI.interrupt();
        }
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public void onPause() {
        super.onPause();
        if (updateGUI != null) {
            updateGUI.interrupt();
        }
    }

    public void onResume() {
        super.onResume();
        if (updateGUI != null && updateGUI.isInterrupted()) {
            updateGUI.start();
        }
    }
}
