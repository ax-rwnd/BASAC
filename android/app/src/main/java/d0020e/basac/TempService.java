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
        Log.d(TAG, "initialize");
        mData = dm;
    }

    public void run() {

        while (true) {
            mData.notifyView();
            //mState.incrementValue(5);
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
