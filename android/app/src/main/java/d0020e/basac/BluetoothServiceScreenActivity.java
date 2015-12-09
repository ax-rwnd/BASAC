package d0020e.basac;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class BluetoothServiceScreenActivity extends AppCompatActivity {

    private BluetoothService mService;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_service_screen);

    }
    @Override
    protected void onStart() {
        super.onStart();
        setupService();
    }

    private void setupService() {
        if (mService == null) {
            mService = new BluetoothService(this, mHandler);
        }
    }

}
