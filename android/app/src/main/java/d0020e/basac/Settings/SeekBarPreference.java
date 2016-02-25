package d0020e.basac.Settings;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by WeeDzCokie on 2016-02-22.
 */
public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener{
    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
