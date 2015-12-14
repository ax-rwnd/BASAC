package d0020e.basac;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

public class SettingsScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_1);
        if (!checkBox.isChecked()) {
            checkBox.setChecked(true);
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was checked
        switch(view.getId()) {
            case R.id.checkbox_1:
                if (checked) {
                    Log.i("box 1 :", "checked");
                }
                else {
                    Log.i("box 1 :", "NOT checked");
                }
                break;
            case R.id.checkbox_2:
                if (checked) {
                    Log.i("box 2 :", "checked");
                }
                else {
                    Log.i("box 2 :", "NOT .checked");
                }
                break;
        }
    }

}
