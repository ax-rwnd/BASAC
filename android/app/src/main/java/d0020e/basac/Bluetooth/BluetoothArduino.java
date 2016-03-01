package d0020e.basac.Bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;
import d0020e.basac.DataModel;
import d0020e.basac.DataStore;

/**
 * Created by WeeDzCokie on 2016-03-01.
 */
public class BluetoothArduino {
    private static final String TAG = "BluetoothArduino";

    private ArduinoReceiver arduinoReceiver = new ArduinoReceiver();

    private Context mContext;
    private String device_address;

    String[] fields = null;
    //String[] s = null; //pachube String
    String result;
    String data = null;
    String HeartBeat;
    float tempin,tempout;
    int X_axis,Y_axis,Z_axis;


    // shared preferences variables
    boolean viberCheckbox = false;
    boolean soundcheckbox = false;
    boolean pachubecheckbox = false;
    boolean localstorage = false;
    String Emergencycontact;
    String Globaltimeout;
    int Global_Time;
    String emailaddress;
    String Skinlowerthresholdtemp;
    String Skinupperthresholdtemp;
    String Environmentlowerthresholdtemp;
    String Environmentupperthresholdtemp;
    String cogasminimum;
    String cogasmaximum;

    // sensor variables
    boolean Emergencybutton = false;
    boolean Fallstatus  = false;
    int Max_heartrate =210,Min_heartrate = 40;
    float max_bodytempin,min_bodytempin,max_envtemp,min_envtemp;

    ArrayList<Float> flo= new ArrayList<>();
    ArrayList<Float> values1= new ArrayList<>();
    ArrayList<Long> time= new ArrayList<>();
    ArrayList<Long> time1= new ArrayList<>();

    public BluetoothArduino(Context c, String address) {
        Log.d(TAG, "Constructor()");
        mContext = c;
        device_address = address;

        // in order to receive broadcasted intents we need to register our receiver
        mContext.registerReceiver(arduinoReceiver, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));
        // this is how you tell Amarino to connect to a specific BT device from within your own code
        Amarino.connect(mContext, device_address);
    }

    public void stop() {
        // if you connect in onStart() you must not forget to disconnect when your app is closed
        Amarino.disconnect(mContext, device_address);

        // do never forget to unregister a registered receiver
        mContext.unregisterReceiver(arduinoReceiver);
    }

    public void turnonvibe() {
        int om = 220;
        Amarino.sendDataToArduino(mContext, device_address, 'A', om);
    }

    // Get the Shared Preferences Values
    private void getPrefs() {
        // Get the xml/preferences.xml preferences
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        viberCheckbox = prefs.getBoolean("checkboxPref", true);
        soundcheckbox = prefs.getBoolean("soundcheckboxPref", true);
        pachubecheckbox = prefs.getBoolean("pachubecheckboxPref", true);
        localstorage = prefs.getBoolean("localcheckboxPref", false);
        Emergencycontact = prefs.getString("emergencycontact","123456789");
        emailaddress  = prefs.getString("emailaddress", "");
        Globaltimeout = prefs.getString("timeout","60");
        Global_Time = Integer.parseInt(Globaltimeout);
        Global_Time = (Global_Time)*1000;     // Global time out

        Skinlowerthresholdtemp = prefs.getString("skinlowerthresholdtemp","10");
        Skinupperthresholdtemp = prefs.getString("skinupperthresholdtemp", "35");
        Environmentlowerthresholdtemp = prefs.getString("Environmentlowerthresholdtemp", "-50");
        Environmentupperthresholdtemp = prefs.getString("Environmentupperthresholdtemp", "50");

        cogasminimum = prefs.getString("cogasminimum", "0");
        cogasmaximum = prefs.getString("cogasmaximum", "100");

    }


    /**
     * ArduinoReceiver is responsible for catching broadcasted Amarino
     * events.
     *
     * It extracts data from the intent.
     */
    public class ArduinoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(AmarinoIntent.ACTION_RECEIVED.equals(intent.getAction()))
            {
                // the type of data which is added to the intent
                final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
                if (dataType == AmarinoIntent.STRING_EXTRA){
                    data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);

                    if (data != null){


                        String patternStr = ","; //separator value
                        fields = data.split(patternStr);//array where values will be stored.
                        //s = fields;   // Copy of string
                        char c = fields[0].charAt(0);

                        if((c == '@'))    // check if the data is in correct format

                        {
                            // user name text field
                            //String text = "\u00A0"+"User name:"+"\n" +"\u00A0"+username +"\n"+ "\u00A0"+"Login Time:"+"\n"+ "\u00A0"+Starttime+"\n"+ "\u00A0"+"Status:"+ "\u00A0"+"connected";
                            String text = "\u00A0"+"User name:"+"\u00A0"+"WeeDz" +"\n"+ "\u00A0"+"Login time:"+ "\u00A0"+ System.currentTimeMillis()+"\n"+ "\u00A0"+"Status:"+ "\u00A0"+"connected";
                            //user.setText(text);

                            //  pachube String
                            /*int[] indices = { 1,2,3,4,5};
                            int length = indices.length;
                            StringBuilder str = new StringBuilder();
                            for (int i=0; i<length; i++) {
                                str.append(s[indices[i]]);
                                if (i+1 != length) str.append(",");
                            }
                            result = str.toString();

                            Timer pachhube = new Timer();
                            pachhube.schedule(new TimerTask()
                            {
                                public void run()
                                {
                                    if(pachubecheckbox == true)
                                    {
                                        coms.Cosm(result);  // Update to Pachube server

                                    }
                                }
                            },60000); //Every 60 seconds

                            Timer t2 = new Timer();
                            t2.schedule(new TimerTask()
                            {
                                public void run()
                                {
                                    if(localstorage == true)
                                    {
                                        coms.savedata(fields,username,Long,Lat);	  // save data into local database
                                    }
                                }
                            },60000); //Every 60 seconds*/

                            if(fields[1] != null && !fields[1].isEmpty() && !fields[1].trim().isEmpty())  // Temperature in
                            {
                                float f = Float.valueOf(fields[1].trim());
                                tempin = Float.valueOf(fields[1].trim());

                                // ------ Set value in DataModel ------
                                DataModel.getInstance().setValue(DataStore.VALUE_SKIN_TEMPERATURE, f);

                                values1.add((float) Math.round(tempin));
                                time1.add(System.currentTimeMillis());

                                //tv3.setText(tempin+" "+(char) 0x00B0 +"C" );  //tv2
                                //tv3.setTextColor(Color.BLACK);
                                max_bodytempin = Float.valueOf(Skinupperthresholdtemp.trim());
                                min_bodytempin = Float.valueOf(Skinlowerthresholdtemp.trim());
                                if( tempin >= max_bodytempin)
                                {
                                    //tv3.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFF62217));
                                }
                                else
                                {
                                    //tv3.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFF59E817));
                                }

                            }

                            if(fields[2] != null && !fields[2].isEmpty() && !fields[2].trim().isEmpty())  // Temperature out
                            {
                                float f = Float.valueOf(fields[2].trim());
                                tempout = Float.valueOf(fields[2].trim());

                                // ------ Set value in DataModel ------
                                DataModel.getInstance().setValue(DataStore.VALUE_ENV_TEMPERATURE, f);

                                flo.add((float) Math.round(f));
                                time.add(System.currentTimeMillis());

                                max_envtemp = Float.valueOf(Environmentupperthresholdtemp.trim());
                                min_envtemp = Float.valueOf(Environmentlowerthresholdtemp.trim());

                                //tv4.setText(f+" "+(char) 0x00B0 +"C" );  //tv3
                                //tv4.setTextColor(Color.BLACK);
                                if( f >= max_envtemp)
                                {
                                    //tv4.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFF62217));
                                }
                                else
                                {
                                    //tv4.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFF59E817));
                                }
                            }
                            if(fields[3] != null && !fields[3].isEmpty() && !fields[3].trim().isEmpty())  // Humidity
                            {
                                float f = Float.valueOf(fields[3].trim());

                                // ------ Set value in DataModel ------
                                DataModel.getInstance().setValue(DataStore.VALUE_HUMIDITY, f);

                                //tv1.setText(f+" "+"% rH" );
                                //tv1.setTextColor(Color.BLACK);
                                if( (f > 80) || (f < 20))
                                {
                                    //tv1.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFF62217));
                                }
                                else
                                {
                                    //tv1.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFF59E817));
                                }
                            }

                            if(fields[4] != null && !fields[4].isEmpty() && !fields[4].trim().isEmpty())  // Heart beat
                            {
                                int heart_beat = Integer.parseInt(fields[4]);

                                // ------ Set value in DataModel ------
                                DataModel.getInstance().setValue(DataStore.VALUE_HEARTRATE, heart_beat);

                                if((heart_beat <= 210) && (heart_beat >= 38) ){
                                    //tv2.setText(fields[4]+" "+"bpm" );
                                    //tv2.setTextColor(Color.BLACK);
                                    //tv2.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFF59E817));
                                    HeartBeat = Integer.toString(heart_beat);
                                }
                            }

                            if(fields[5] != null && !fields[5].isEmpty() && !fields[5].trim().isEmpty())    // CO gas
                            {
                                float f = Float.valueOf(fields[5].trim());

                                // ------ Set value in DataModel ------
                                DataModel.getInstance().setValue(DataStore.VALUE_CO, f);

                                //tv5.setText(f+" "+"ppm" );
                                //tv5.setTextColor(Color.BLACK);
                                //tv5.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFF59E817));
                            }
                            if(fields[6] != null && !fields[6].isEmpty() && !fields[6].trim().isEmpty())
                            {
                                int n = Integer.parseInt(fields[6]);
                                if(n == 1)
                                {

                                    //Emergencybutton = false;
                                }
                                else
                                {
                                    //Emergencybutton = true;
                                }
                            }
                            if(fields[7] != null && !fields[7].isEmpty() && !fields[7].trim().isEmpty())
                            {
                                float f = Float.valueOf(fields[7].trim());
                                Log.d(TAG, "unknown value, fields[7]: " + f);
                                /*if(f > 4.55)
                                {
                                    percentage = "100";
                                    imageButton.setImageDrawable((Drawable)getResources().getDrawable(R.drawable.h));
                                }
                                if(f > 4.35 && f < 4.55)
                                {
                                    percentage = "75";
                                    imageButton.setImageDrawable((Drawable)getResources().getDrawable(R.drawable.mh));
                                }
                                if(f > 4.0 && f < 4.35)
                                {
                                    percentage = "50";
                                    imageButton.setImageDrawable((Drawable)getResources().getDrawable(R.drawable.ml));
                                }
                                if(f < 4.0)
                                {
                                    percentage = "25";
                                    imageButton.setImageDrawable((Drawable)getResources().getDrawable(R.drawable.l));
                                }*/
                            }
                            if(fields[8] != null && !fields[8].isEmpty() && !fields[8].trim().isEmpty())
                            {
                                //X_axis = Integer.parseInt(fields[8]);
                            }
                            if(fields[9] != null && !fields[9].isEmpty() && !fields[9].trim().isEmpty())
                            {
                                //X_axis = Integer.parseInt(fields[9]);
                            }
                            if(fields[10] != null && !fields[10].isEmpty() && !fields[10].trim().isEmpty())
                            {
                                //Z_axis = Integer.parseInt(fields[10]);
                            }
                        }
                    }
                }
            }
        }

    }

}
