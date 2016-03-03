package d0020e.basac;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class HomeScreenActivity extends AppCompatActivity {
    private static final String TAG = "HomeScreen";

    static {
        System.loadLibrary("crypto");
        System.loadLibrary("ssl");
        System.loadLibrary("androidmkc");
    }

    private native int generateContent(String content, String ipath, String opath);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DataStore ds = (DataStore)getApplication();
        ds.mState = new StateController(ds);

        generateContent("/ndn/hello-world", Environment.getExternalStorageDirectory().getPath() + "/infile.txt", Environment.getExternalStorageDirectory().getPath() + "/test.ndntlv");

        Button settings = (Button)findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_data) {
            Intent intent = new Intent(this, DataScreenActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void start_monitor(View view) {
        DataStore ds = (DataStore)getApplication();
        ds.mState.stop();
        ds.mState = null;
        startService(new Intent(this, StateController.class));
    }

    public void stop_monitor(View view) {
        Intent intent = new Intent(this, StateController.class);
        intent.putExtra("STOP", "STOP");
        startService(intent);
    }

    public void startDataScreen(View view) {
        Intent intent = new Intent(this, DataScreenActivity.class);
        startActivity(intent);
    }

    public void startSettingsScreen(View view) {
        Intent intent = new Intent(this, SettingsScreenActivity.class);
        startActivity(intent);
    }



    public void onResume() {
        super.onResume();
    }

}
