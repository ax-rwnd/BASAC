package d0020e.basac.Settings;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by WeeDzCokie on 2016-02-22.
 */
public class ListPreference extends android.preference.ListPreference {

    public ListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAttachedToActivity() {
        setSummary(getEntry());
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        setSummary(getEntry());
    }
}
