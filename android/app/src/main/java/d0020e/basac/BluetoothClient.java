package d0020e.basac;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * TODO: Display connection status to the user, notify if connection failed
 */

public class BluetoothClient {
    private static final String NAME = "BASAC";
    private static final UUID MY_UUID = UUID.fromString("67f071e1-dbbc-47e6-903e-769a5e262ad2");
    private static final String TAG = "BTClient";

    private BluetoothAdapter mBluetoothAdapter;

    private static ConnectedThread mConnectedThread;
    private ConnectThread mConnectThread;
    private ReconnectThread mReconnectThread;

    public static BluetoothDevice mDevice = null;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;

    private boolean intendedStop = false;

    public static int mState = STATE_NONE;

    private static Handler mHandler = null;

    private Context mContext;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage(): " + msg.what);
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                        switch (msg.arg1) {
                            case BluetoothClient.STATE_CONNECTED:
                                break;
                            case BluetoothClient.STATE_CONNECTING:
                                break;
                            case BluetoothClient.STATE_LISTEN:
                            case BluetoothClient.STATE_NONE:
                                break;
                        }
                        break;
                    case MESSAGE_READ:
                        if (msg.obj != null) {

                            byte[] readBuf = (byte[]) msg.obj;
                            String readMessage = new String(readBuf, 0, msg.arg1);

                            try {
                                JSONObject json = new JSONObject(readMessage);
                                DataModel.getInstance().setValue(DataStore.VALUE_OXYGEN, json.getInt("oxygen"));
                                DataModel.getInstance().setValue(DataStore.VALUE_CO, json.getInt("co"));
                                DataModel.getInstance().setValue(DataStore.VALUE_AIRPRESSURE, json.getInt("airpressure"));
                                DataModel.getInstance().setValue(DataStore.VALUE_TEMPERATURE, json.getInt("temperature"));
                                DataModel.getInstance().setValue(DataStore.VALUE_HEARTRATE, json.getInt("heartrate"));
                                DataModel.getInstance().setValue(DataStore.VALUE_HUMIDITY, json.getInt("humidity"));
                                DataModel.getInstance().setUpdate();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // construct a string from the valid bytes in the buffer
                            /*Log.d(TAG, "Handler() msgRead: " + readMessage);
                            try {
                                DataModel.getInstance().setValue(DataStore.VALUE_OXYGEN, Integer.parseInt(readMessage));
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                                editor.putInt("data_"+DataStore.VALUE_OXYGEN, Integer.parseInt(readMessage));
                                editor.apply();
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "NumberFormatException: " + readMessage);
                            }
                            */
                        }
                        break;
                    case MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        // construct a string from the buffer
                        String writeMessage = new String(writeBuf);
                        Log.d(TAG, "Handler() msgWrite: " + writeMessage);
                        break;
                    case MESSAGE_DEVICE_NAME:
                        // save the connected device's name
                        Log.d(TAG, "Handler() msgDeviceName: ");
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };

    public BluetoothClient(Context c) {
        Log.d(TAG, "Bluetooth Connecting");
        mContext = c;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            String address = sharedPref.getString("device_address", null);
            Log.d(TAG, "Bluetooth device: " + address);
            if (address != null) {
                BluetoothClient.mDevice = mBluetoothAdapter.getRemoteDevice(address);
                connect(BluetoothClient.mDevice);
            } else {
                Toast.makeText(mContext, "No device address", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Address = null, terminating");
                stop();
            }
        } else {
            Toast.makeText(mContext, "Enable bluetooth", Toast.LENGTH_SHORT).show();
            stop();
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        intendedStop = false;
        //Log.d(TAG, "connect to: " + device);
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    private void setState(int state) {
        mState = state;
    }

    public int getState() {
        return mState;
    }

    public void setStop() {
        intendedStop = true;
    }

    public synchronized void stop() {
        Log.d(TAG, "stop()");
        setState(BluetoothClient.STATE_NONE);
        if (mReconnectThread != null) {
            mReconnectThread.interrupt();
            mReconnectThread = null;
        }
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        Toast.makeText(mContext, "Bluetooth disconnected", Toast.LENGTH_SHORT).show();
        NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (StateController.serviceRunning) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
            mBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle("BASAC")
                    .setContentText("BASAC service started")
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(
                            PendingIntent.getActivity(
                                    mContext,
                                    0,
                                    new Intent(mContext, HomeScreenActivity.class),
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            )
                    )
                    .addAction(
                            R.drawable.ic_notifications_black_24dp,
                            "Stop",
                            PendingIntent.getService(
                                    mContext,
                                    0,
                                    new Intent(mContext, StateController.class)
                                            .putExtra("STOP", "STOP"),
                                    PendingIntent.FLAG_UPDATE_CURRENT));
            mNotifyMgr.notify(DataStore.NOTIFICATION_SERVICE_RUNNING, mBuilder.build());
        } else {
            mNotifyMgr.cancel(DataStore.NOTIFICATION_BLUETOOTHCLIENT);
        }

        if (!intendedStop) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle("Bluetooth")
                    .setContentText("Bluetooth connection lost")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(
                            PendingIntent.getActivity(
                                    mContext,
                                    0,
                                    new Intent(mContext, SettingsScreenActivity.class),
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            )
                    )
                    .setAutoCancel(true);
            mNotifyMgr.notify(DataStore.NOTIFICATION_BLUETOOTH_LOST, mBuilder.build());
        }
    }

    /**
     * TODO: Retry connection X times before stopping threads
     */
    private void connectionLost() {
        Log.d(TAG, "Connection lost");
        BluetoothClient.this.stop();

        mReconnectThread = new ReconnectThread();
        mReconnectThread.start();
    }
    private void connectionFailed() {
        Log.d(TAG, "Connection failed");
        intendedStop = true;
        BluetoothClient.this.stop();
    }

    public synchronized void manageConnectedSocket(BluetoothSocket socket, BluetoothDevice device) {
        Log.d(TAG, "connected");
        Toast.makeText(mContext, "Connected", Toast.LENGTH_SHORT).show();

        // Show notification when bluetooth is connected
        NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        if (StateController.serviceRunning) {
            mNotifyMgr.cancel(DataStore.NOTIFICATION_BLUETOOTHCLIENT);
            // Merge service and bluetooth notification
            mBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle("BASAC")
                    .setContentText("BASAC service started, Bluetooth connected")
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(
                            PendingIntent.getActivity(
                                    mContext,
                                    0,
                                    new Intent(mContext, HomeScreenActivity.class),
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            )
                    )
                    .addAction(
                            R.drawable.ic_notifications_black_24dp,
                            "Stop",
                            PendingIntent.getService(
                                    mContext,
                                    0,
                                    new Intent(mContext, StateController.class)
                                            .putExtra("STOP", "STOP"),
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                    .addAction(
                            R.drawable.ic_notifications_black_24dp,
                            "Disconnect",
                            PendingIntent.getService(
                                    mContext,
                                    1,
                                    new Intent(mContext, StateController.class)
                                            .putExtra("STOP", "BLUETOOTH"),
                                    PendingIntent.FLAG_UPDATE_CURRENT));
            mNotifyMgr.notify(DataStore.NOTIFICATION_SERVICE_RUNNING, mBuilder.build());
        } else {
            // Show notification for bluetooth
            mBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle("Bluetooth")
                    .setContentText("Bluetooth is connected")
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(
                            PendingIntent.getActivity(
                                    mContext,
                                    0,
                                    new Intent(mContext, HomeScreenActivity.class),
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            )
                    )
                    .addAction(
                            R.drawable.ic_notifications_black_24dp,
                            "Disconnect",
                            PendingIntent.getService(
                                    mContext,
                                    0,
                                    new Intent(mContext, StateController.class)
                                            .putExtra("STOP", "BLUETOOTH"),
                                    PendingIntent.FLAG_UPDATE_CURRENT));
            mNotifyMgr.notify(DataStore.NOTIFICATION_BLUETOOTHCLIENT, mBuilder.build());
        }
        // Cancel any reconnect thread running
        if (mReconnectThread != null) {
            mReconnectThread.interrupt();
            mReconnectThread = null;
        }
        // Cancel the thread that completed the connection
       if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        // Send the name of the connected device back to the UI Activity
        Message msg = handler.obtainMessage(2);
        Bundle bundle = new Bundle();
        bundle.putString(NAME, device.getName());
        msg.setData(bundle);
        handler.sendMessage(msg);

        setState(BluetoothClient.STATE_CONNECTED);
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private ByteArrayOutputStream bos = new ByteArrayOutputStream();
        private ObjectOutput out = null;
        private ObjectInput in = null;

        private byte[] yourBytes = new byte[1024];

        public ConnectedThread(BluetoothSocket socket) {

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
                out = new ObjectOutputStream(bos);
            } catch (IOException e) {
                Log.e(TAG,"ConnectedThread() failed",e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Looper.prepare();
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                handler.obtainMessage(MESSAGE_WRITE, buffer.length, -1, buffer).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Exception during write");
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                Log.d(TAG, "ConnectedThread cancel()");
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Error in ConnectThread.run() RfcommSocket");
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            Looper.prepare();
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                //mStatus.setText("Connected");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.d(TAG, "ConnectThread run() mmSocket.close()");
                }
                if (mReconnectThread == null) {
                    connectionFailed();
                }
                return;
            }

            synchronized (BluetoothClient.this) {
                mConnectThread = null;
            }

            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket, mmDevice);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                Log.d(TAG, "ConnectThread cancel()");
                if (mmSocket != null) {
                    mmSocket.close();
                }
            } catch (IOException e) {
                Log.d(TAG, "ConnectThread cancel() mmSocket.close()");
            }
        }
    }

    /**
     * TODO: show notification/toast message when attempting reconnect
     */
    private class ReconnectThread extends Thread {
        int reconnectAttempt = 0;
        int maxReconnectAttempt;
        int timeout;
        boolean reconnect;
        boolean interruptStatus = false;

        public ReconnectThread() {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            maxReconnectAttempt = Integer.parseInt(sharedPref.getString("pref_key_settings_bluetooth_reconnect_attempts", "5"));
            timeout = Integer.parseInt(sharedPref.getString("pref_key_settings_bluetooth_timeout", "5")) * 1000;
            reconnect = sharedPref.getBoolean("pref_key_settings_bluetooth_reconnect", true);
        }

        public void run() {
            Looper.prepare();
            while (reconnectAttempt < maxReconnectAttempt && mConnectedThread == null) {
                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled() || !reconnect || intendedStop) {
                    break;
                }
                setState(BluetoothClient.STATE_NONE);
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d(TAG, "ReconnectThread.run() interrupted");
                    interruptStatus = true;
                    break;
                }
                Log.d(TAG, "ReconnectThread, attempt: " + (reconnectAttempt+1));
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
                String address = sharedPref.getString("device_address", null);
                BluetoothClient.mDevice = mBluetoothAdapter.getRemoteDevice(address);
                connect(BluetoothClient.mDevice);
                reconnectAttempt++;
                if (mConnectedThread != null) {
                    break;
                }
            }
            Log.d(TAG, "Exit ReconnectThread");
            if (mConnectedThread == null && !interruptStatus) {
                connectionFailed();
            }
        }

    }

}
