package d0020e.basac;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
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
    private static final UUID MY_UUID = UUID.fromString("b5cb9ed6-176c-45ae-b129-38ae7665e485");


    private BluetoothAdapter mAdapter;
    private final Handler mHandler;

    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private int mState;
    private static final int STATE_NONE = 0;

    private int[] data;

    public BluetoothService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        mState = STATE_NONE;

        data = new int[5];
        data[0] = 1;
        data[1] = 2;
        data[2] = 3;
        data[3] = 4;
        data[4] = 5;
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
