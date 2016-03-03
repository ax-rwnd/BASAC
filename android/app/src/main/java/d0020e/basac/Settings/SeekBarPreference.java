package d0020e.basac.Settings;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by WeeDzCokie on 2016-02-22.
 */
public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener{

    private static final String androidns = "http://schemas.android.com/apk/res/android";
    private static final String basacns = "http://schemas.android.com/apk/res-auto";

    private SeekBar mSeekBar;
    private TextView mValueText;
    private String mDialogMessage, mSuffix;

    private Context mContext;
    private int mDefault, mMin, mMax, mValue;


    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        int mDialogMessageId = attrs.getAttributeResourceValue(androidns, "dialogMessage", 0);
        mDialogMessage = mDialogMessageId == 0 ? attrs.getAttributeValue(androidns, "dialogMessage") : mContext.getString(mDialogMessageId);

        int mSuffixId = attrs.getAttributeResourceValue(androidns, "text", 0);
        mSuffix = mSuffixId == 0 ? attrs.getAttributeValue(androidns, "text") : mContext.getString(mSuffixId);

        mDefault = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
        mMax = attrs.getAttributeIntValue(basacns, "maxValue", 100);
        mMin = attrs.getAttributeIntValue(basacns, "minValue", 0);
    }

    @Override
    public void onAttachedToActivity() {
        updateSummary();
        super.onAttachedToActivity();
    }

    private void updateSummary() {
        String v = String.valueOf(getPersistedInt(mDefault));
        setSummary("Current value: " + (mSuffix == null ? v : v.concat(" " + mSuffix)));
    }

    @Override
    protected View onCreateDialogView() {
        LinearLayout.LayoutParams params;
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6,6,6,6);

        TextView mSplashText = new TextView(mContext);
        mSplashText.setPadding(30,10,30,10);
        if (mDialogMessage != null) {
            mSplashText.setText(mDialogMessage);
        }
        layout.addView(mSplashText);

        mValueText = new TextView(mContext);
        mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
        mValueText.setTextSize(32);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mValueText, params);

        mSeekBar = new SeekBar(mContext);
        mSeekBar.setOnSeekBarChangeListener(this);
        layout.addView(mSeekBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


        if (shouldPersist()) {
            mValue = getPersistedInt(mDefault);
        }
        mSeekBar.setMax(mMax - mMin);
        mSeekBar.setProgress(mValue - mMin);

        return layout;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        mSeekBar.setMax(mMax - mMin);
        mSeekBar.setProgress(mValue - mMin);
    }
    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        super.onSetInitialValue(restore, defaultValue);
        if (restore) {
            mValue = shouldPersist() ? getPersistedInt(mDefault) : mMin;
        } else {
            mValue = (Integer)defaultValue;
        }
    }
    @Override
    public void showDialog(Bundle state) {
        super.showDialog(state);
        Button positive = ((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shouldPersist()) {
                    mValue = mSeekBar.getProgress() + mMin;
                    persistInt(mValue);
                    callChangeListener(mValue);
                }
                updateSummary();
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String t = String.valueOf(mMin + progress);
        mValueText.setText(mSuffix == null ? t : t.concat(" " + mSuffix));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
