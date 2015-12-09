package d0020e.basac;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by weedz on 2015-12-08.
 */
public class BluetoothService {
    private static final String TAG = "BluetoothService";

    private static final String NAME = "BASAC";
    private static final UUID MY_UUID = UUID.randomUUID();


    private BluetoothAdapter mAdapter;
    private final Handler mHandler;

    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private int mState;
    private static final int STATE_NONE = 0;

    public BluetoothService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        mState = STATE_NONE;
    }

    private class AcceptThread extends Thread {

        private BluetoothServerSocket mServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread failed",e);
            }
            mServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "Begin AcceptThread()" + this);



        }

    }

    private class ConnectThread extends Thread{
        public ConnectThread() {

        }

        public void run() {

        }
    }

    private class ConnectedThread extends Thread {
        public ConnectedThread() {

        }

        public void run() {

        }
    }

}
