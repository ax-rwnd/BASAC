package d0020e.basac;

import android.util.Log;

/**
 * Created by weedz on 2016-01-18.
 */
public class UpdateDataGUIService extends Thread {

    private static String TAG = "UpdateDataGUIService";

    private StateController mState;
    private DataModel mData;

    public UpdateDataGUIService(StateController sc) {
        Log.d(TAG, "initialize");
        mState = sc;
    }

    public UpdateDataGUIService(DataModel dm) {
        Log.d(TAG, "initialize");
        mData = dm;
    }

    public void run() {
        Log.d(TAG, "run()");
        while (true) {
            mData.notifyView();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(TAG, "run() interupted", e);
                break;
            }
        }
    }



    public void cancel() {

        Log.d(TAG,"cancel()");

    }

}
