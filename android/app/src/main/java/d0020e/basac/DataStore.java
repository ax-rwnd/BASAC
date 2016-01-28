package d0020e.basac;

import android.app.Application;
import android.util.Log;

/**
 * Created by WeeDzCokie on 2016-01-28.
 */
public class DataStore extends Application {

    public StateController mState;

    @Override
    public void onCreate() {
        super.onCreate();
        getSharedPreferences(getString(R.string.preference_data_key), MODE_PRIVATE);
        DataModel.getInstance().addValue(0);
        mState = new StateController();

    }

    public StateController getState() {
        return mState;
    }

    public void appMethod() {
        Log.d("App", "log");
    }

}
