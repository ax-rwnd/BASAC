package d0020e.basac;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class containing the data required to submit a report and push it to the CCN-lite network.
 */
public class UserIncidentReport {
    private int warningId;
    private long timeStamp;
    private String reportMessage;
    private JSONData reportJson;
    private Context mContext;
    private SendAlarmTCP sat;
    private StateController mState;

    public UserIncidentReport(Context c, int warningId, String message) {
        mContext = c;
        new connectTask().execute("");
        this.warningId = warningId; //the kind of accident recorded
        this.timeStamp = System.currentTimeMillis();
        this.reportMessage = message;
        reportJson = new JSONData();
        StateController.setWarningState(warningId, false);
    }

    public void submitReport(StateController mState) {
        this.mState = mState;
        reportJson.putData("AccidentTimeStamp", timeStamp);
        reportJson.putData("AccidentType", warningId);
        reportJson.putData("AccidentMessage", reportMessage);
        reportJson.updateTimestamp();
        Log.d("Report", reportJson.toString());
        Toast.makeText(mContext, "Report sent", Toast.LENGTH_SHORT).show();

        String filename = "report_" + getTimeStamp();
        String string = getTimeStamp() + "::" + getType() + "::"+getReportMessage();

        FileOutputStream outputStream;
        try {
            outputStream = mContext.openFileOutput(filename+".txt", Context.MODE_PRIVATE);
            outputStream.write(reportJson.toString().getBytes());
            outputStream.close();
            Log.d("Files Directory Report", String.valueOf(mContext.getFilesDir()));
            Log.d("Report", string);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // write file to sdcard
        /*try {
            File basacFolder = new File(Environment.getExternalStorageDirectory(), "BASAC");
            if (!basacFolder.exists()) {
                basacFolder.mkdir();
            }
            try {
                File dataFile = new File(basacFolder, "data_" + getTimeStamp() + ".txt");
                dataFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(dataFile, false);
                try {
                    //fos.write(reportJson.toString().getBytes());
                    fos.write(string.getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mState.makeContent(Environment.getExternalStorageDirectory().toString() + "/BASAC/data_" + getTimeStamp() + ".txt",
                Environment.getExternalStorageDirectory().toString() + "/ccn-lite/data_" + getTimeStamp() + ".ndntlv");*/

        this.mState.makeContent(mContext.getFilesDir() + "/" + filename + ".txt",
                Environment.getExternalStorageDirectory().toString() + "/ccn-lite/data_" + getTimeStamp() + ".ndntlv");
        transmitToServer(warningId);
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
