package d0020e.basac;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * The "Settings" (not temp settings) activity, GUI elements are build with Fragments
 */
public class SettingsActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
