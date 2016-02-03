package d0020e.basac;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by WeeDzCokie on 2016-02-03.
 */
public class ResetLogFileDialogPreference extends DialogPreference {

    public ResetLogFileDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean result) {
        super.onDialogClosed(result);
        persistBoolean(result);
        if (result) {
            // Delete log file
            if (getContext().deleteFile("data.log")) {
                Log.d("Dialog", "Deleted file data.log");
            } else {
                Log.d("Dialog", "Could not delete file data.log");
            }
        }
    }

}
