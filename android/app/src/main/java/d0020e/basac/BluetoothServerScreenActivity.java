package d0020e.basac;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothServerScreenActivity extends AppCompatActivity {

    private static final String NAME = "BASAC";
    private static final UUID MY_UUID = UUID.fromString("67f071e1-dbbc-47e6-903e-769a5e262ad2");
    private static final String TAG = "BTServer";


    // Demo
    private static boolean demoStart = false;
    private ArrayList<JSONObject> demoData = new ArrayList<>();
    private Thread demoThread;

    public synchronized void beginDemo() {
        if (demoThread != null) {
            demoStart = false;
            demoThread.interrupt();
        }
        demoStart = !demoStart;

        demoThread = new Thread() {
            int currentData = 0;
            public void run() {
                // demo data
                JSONObject json = null;
                try {
                    json = new JSONObject();
                    json.put("oxygen", 21)         // %
                            .put("heartrate", 90)       // Beats / minute
                            .put("temperature", 24)     // C
                            .put("airpressure", 101325)     // Pa, ~ 1 atmosphere
                            .put("humidity", 70)      // %
                            .put("co", 3);        // Carbon monoxide, parts per million
                    demoData.add(json);   // [0] = Normal/safe values

                    json = new JSONObject();
                    json.put("oxygen", 12)
                            .put("heartrate", 140)
                            .put("temperature", 24)
                            .put("airpressure", 101325)
                            .put("humidity", 70)
                            .put("co", 3);
                    demoData.add(json);   // [1] = Low oxygen levels

                    json = new JSONObject();
                    json.put("oxygen", 21)
                            .put("heartrate", 130)
                            .put("temperature", 24)
                            .put("airpressure", 101325)
                            .put("humidity", 70)
                            .put("co", 100);
                    demoData.add(json);   // [2] = High carbon monoxide levels

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                while (demoStart && currentData < demoData.size()) {
                    try {
                        ConnectedThread r;
                        // Synchronize a copy of the ConnectedThread
                        synchronized (this) {
                            if (mState != BluetoothClient.STATE_CONNECTED) return;
                            r = mConnectedThread;
                        }
                        // Perform the write unsynchronized
                        r.write(demoData.get(currentData).toString().getBytes());
                        Thread.sleep(5000);
                        currentData++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error in demoThread()");
                    }
                }
                ConnectedThread r;
                // Synchronize a copy of the ConnectedThread
                synchronized (this) {
                    if (mState != BluetoothClient.STATE_CONNECTED) return;
                    r = mConnectedThread;
                }
                // Perform the write unsynchronized
                r.write(demoData.get(0).toString().getBytes());     // Set default/safe values

                Log.d(TAG, "demo end");
            }
        };
        demoThread.start();
    }

    //

    private ConnectedThread mConnectedThread;
    private AcceptThread mAcceptThread;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;

    private BluetoothAdapter mBluetoothAdapter;

    private StringBuffer mOutStringBuffer = null;

    private BluetoothHandler mHandler = new BluetoothHandler(this);

    private static class BluetoothHandler extends Handler {
        private final WeakReference<BluetoothServerScreenActivity> mReference;
        private BluetoothHandler(BluetoothServerScreenActivity bc) {
            mReference = new WeakReference<>(bc);
        }
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage(): " + msg.what);
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothClient.STATE_CONNECTED:
                            Toast.makeText(mReference.get(), "State CONNECTED", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothClient.STATE_CONNECTING:
                            Toast.makeText(mReference.get(), "State CONNECTING", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothClient.STATE_LISTEN:
                        case BluetoothClient.STATE_NONE:
                            break;
                    }
                    break;case MESSAGE_READ:
                    if (msg.obj != null) {
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        Log.d(TAG, "Handler() msgRead: " + readMessage);
                        //mReference.get().mDataModel.setTestValue(Integer.parseInt(readMessage));
                    }
                    break;
                case MESSAGE_WRITE:
                    //ObjectOutputStream oos = new ObjectOutputStream()
                    if (msg.obj != null) {
                        byte[] writeBuf = (byte[]) msg.obj;
                        // construct a string from the buffer
                        String writeMessage = new String(writeBuf);
                        Log.d(TAG, "Handler() msgWrite: " + writeMessage);
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    Log.d(TAG, "Handler() msgDeviceName: ");
                    break;
            }
        }
    }

    private int mState = BluetoothClient.STATE_NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            return;
        }
        setContentView(R.layout.activity_bluetooth_server_screen);

        //Bundle data = getIntent().getExtras();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mState == BluetoothClient.STATE_NONE) {
            // Initialize the send button with a listener that for click events
            Button mSendButton = (Button) findViewById(R.id.bt_server_send);
            mSendButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Send a message using content of the edit text widget
                    TextView view = (TextView) findViewById(R.id.bt_server_data_01);
                    String message = view.getText().toString();
                    sendMessage(message);
                }
            });
            mSendButton = (Button) findViewById(R.id.send_demo_data);
            mSendButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Send a message using content of the edit text widget
                    TextView view = (TextView) findViewById(R.id.bt_server_data_01);
                    String message = view.getText().toString();
                    beginDemo();
                }
            });
            mOutStringBuffer = new StringBuffer("");
            start();
        }
    }

    @Override
    protected void onStop() {
        stop();
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        stop();
        super.onDestroy();
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start() {
        Log.d(TAG, "start");
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(BluetoothClient.STATE_LISTEN);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.d(TAG, "stop");
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        setState(BluetoothClient.STATE_NONE);
    }

    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mState != BluetoothClient.STATE_CONNECTED) {
            Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            write(send);
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void manageConnectedSocket(BluetoothSocket socket, BluetoothDevice device) {
        Log.d(TAG, "connected");
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(2);
        Bundle bundle = new Bundle();
        bundle.putString(NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(BluetoothClient.STATE_CONNECTED);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != BluetoothClient.STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    private void setState(int state) {
        mState = state;
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG,"AcceptThread() failed",e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                    // If a connection was accepted
                    if (socket != null) {
                        // Do work to manage the connection (in a separate thread)
                        manageConnectedSocket(socket, socket.getRemoteDevice());
                        mmServerSocket.close();
                        break;
                    }
                } catch (IOException e) {
                    Log.e(TAG,"run() failed",e);
                    break;
                }

            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG,"AcceptThread cancel() failed",e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG,"ConnectedThread() failed",e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(1, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "connection closed, starting AcceptThread()");
                    mAcceptThread = new AcceptThread();
                    mAcceptThread.start();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write()", e);
            }
        }

        public void writeObject(Object o) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(mmOutStream);
                oos.writeObject(o);
            } catch (IOException e) {
                Log.e(TAG, "Exception during writeObject()", e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.d(TAG, "ConnectThread cancel()");
            }
        }
    }

}
