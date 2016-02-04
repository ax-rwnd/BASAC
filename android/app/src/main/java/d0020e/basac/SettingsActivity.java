package d0020e.basac;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    DataStore ds;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ds = (DataStore)getApplicationContext();
        ds.mState.setContext(this);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public void onResume() {
        super.onResume();
        ds.mState.setContext(this);

    }
}
