package d0020e.basac;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;

/**
 * Created by Sebastian on 17/02/2016.
 */
//TODO: figure out how to shoehorn this into a JSON object and push as seperate content to CCN network.
public class UserIncidentReport {
    private int warningId;
    private long timeStamp;
    private String reportMessage;
    private JSONData reportJson;
    private Context mContext;
    private SendAlarmTCP sat;

    public UserIncidentReport(Context c, int warningId, String message) {
        mContext = c;
        new connectTask().execute("");
        this.warningId = warningId; //the kind of accident recorded
        this.timeStamp = System.currentTimeMillis();
        this.reportMessage = message;
        reportJson = new JSONData();
        StateController.setWarningState(warningId, false);
    }

    public void submitReport() {
        reportJson.putData("AccidentTimeStamp", timeStamp);
        reportJson.putData("AccidentType", warningId);
        reportJson.putData("AccidentMessage", reportMessage);
        reportJson.logJSON();
        Log.d("Report", reportJson.toString());

        String filename = "report:" + getTimeStamp() + ".txt";
        String string = getTimeStamp() + "::" + getType() + "::"+getReportMessage();
        FileOutputStream outputStream;

        try{
            outputStream = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
            Log.d("Files Directory Report", String.valueOf(mContext.getFilesDir()));
            Log.d("Report", string);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //transmitToServer(warningId);
    }

    public void transmitToServer(int warningId) {
        if (sat != null) {
            sat.sendAlarm("WarningId: " + Integer.toString(warningId));
        } else {
            Log.d("Report", "Could not send report");
        }
    }

    private class connectTask extends AsyncTask<String, String, SendAlarmTCP> {
        @Override
        protected SendAlarmTCP doInBackground(String... message) {
            sat = new SendAlarmTCP(new SendAlarmTCP.OnMessageReceived() {
                @Override
                public void messageReceived(String message) {
                    publishProgress(message);
                }
            });
            sat.run();
            sat.sendAlarm("WarningId: ...");
            return null;
        }

    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getReportMessage() {
        return reportMessage;
    }

    public int getType() {
        return warningId;
    }
}
