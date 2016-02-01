package d0020e.basac;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

public class DataMonitor extends IntentService {
    private static final String TAG = "DataMonitor";


    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private IBinder mBinder = new LocalBinder();

    private BluetoothClient mBluetoothClient;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DataMonitor(String name) {
        super(name);
    }

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            long endTime = System.currentTimeMillis() + 2000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {}
                }
            }

            stopSelf(msg.arg1);
        }

    }

    public class LocalBinder extends Binder {
        DataMonitor getService() {
            return DataMonitor.this;
        }
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service starting", Toast.LENGTH_SHORT).show();

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String dataString = intent.getDataString();
        try {
            //mBluetoothClient = new BluetoothClient();
        } catch (Exception e) {
            Log.e(TAG, "BluetoothClient() failed to start");
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        // TODO: Cleanup Bluetooth listeners and threads
        Toast.makeText(this, "Service done", Toast.LENGTH_SHORT).show();
    }

}
