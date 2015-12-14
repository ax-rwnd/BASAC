package d0020e.basac;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class HomeScreenActivity extends AppCompatActivity {

    private DataModel dataModel;
    private StateController stateController;
    private Button dataButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("HomeScreen", "Datamodel Created");
        /* Starts the StateController as a seperate thread*/
        /*new Thread(new Runnable() {
            public void run() {
                dataModel = new DataModel();
                stateController = new StateController(dataModel);
                // TODO: implement observer-observable pattern between stateController & Bluetooth manager.
            }
        }).start();*/

        dataModel = new DataModel();
        stateController = new StateController(dataModel);

        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        /* Creates the actionbutton for checking/connecting through bluetooth. */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Bluetooth goes here.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        dataButton = (Button) findViewById(R.id.action_data);
        dataButton.setText("DATA");

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
            Intent intent = new Intent(this, SettingsScreenActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_data) {
            Intent intent = new Intent(this, DataScreenActivity.class);
            intent.putExtra("dataModel", dataModel);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startDataScreen(View view) {
            Intent intent = new Intent(this, DataScreenActivity.class);
            intent.putExtra("dataModel", dataModel);
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
