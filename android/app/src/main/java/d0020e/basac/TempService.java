package d0020e.basac;

import android.util.Log;

/**
 * Created by weedz on 2016-01-18.
 */
public class TempService extends Thread {

    private static String TAG = "TempService";

    private StateController mState;
    private DataModel mData;

    public TempService(StateController sc) {

        Log.d(TAG, "initialize");
        mState = sc;

    }

    public TempService(DataModel dm) {
        Log.d(TAG, "Started");
        mData = dm;
    }

    public void run() {

        while (true) {
            Log.d(TAG, "run()");
            mData.incrementTestValue();
            //mState.incrementValue(5);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Log.e(TAG, "run() failed", e);
            }
        }

    }

    public void cancel() {

        Log.d(TAG,"cancel()");

    }

}
