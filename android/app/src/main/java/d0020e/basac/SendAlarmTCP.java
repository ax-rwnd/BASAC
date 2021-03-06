package d0020e.basac;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class SendAlarmTCP {
    private String serverMessage = "NUll";
    private String TAG="TCP/IP client";
    private Socket socket;
    private OnMessageReceived mRecvListener = null;
    public String serverIP = "192.168.43.149";
    public int serverPort = 1337;
    private boolean mRun = false;

    PrintWriter outgoing;
    BufferedReader incoming;

    public SendAlarmTCP(OnMessageReceived listener){
        this.mRecvListener=listener;
    }

    public void sendAlarm(String msg){
        Log.d(TAG,"Trying to send: " + msg);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(DataStore.mState.getInstance().getContext());
        serverIP = pref.getString("reports_ip_address", "192.168.43.149");
        serverPort = Integer.parseInt(pref.getString("reports_port_number", "1337"));
        if(outgoing != null && !outgoing.checkError()){
            outgoing.println(msg);
            outgoing.flush();
        }
    }

    public void haltClient(){
        this.mRun= false;
    }

    public void run() {
        this.mRun = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(serverIP);
            Log.d(TAG,"Client connecting...");
            socket = new Socket(serverAddr, serverPort);

            try {
                outgoing = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                Log.d(TAG,"Client sent message");

                incoming = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while(mRun){
                    incoming.readLine();
                    if(serverMessage != null && mRecvListener != null){
                        mRecvListener.messageReceived(serverMessage);
                    }
                    serverMessage = null;
                }

                Log.d(TAG,"Message received from server: " + serverMessage);
            } catch (Exception e){
                Log.d(TAG,"(Server)Error occured pls " + e);
            } finally {
                socket.close();
            }

        } catch (Exception e) {
            Log.d(TAG,"Error clientish " + e);
        }
    }

    public interface OnMessageReceived {
        void messageReceived(String message);
    }

}



