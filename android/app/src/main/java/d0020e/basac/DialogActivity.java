package d0020e.basac;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DialogActivity extends Activity {
    private static final String TAG = "DialogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        Button cancel = (Button)findViewById(R.id.cancel_report);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancel");
                finish();
            }
        });
        Button send = (Button)findViewById(R.id.send_report);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Send");
                Toast.makeText(getApplicationContext(), "Report sent", Toast.LENGTH_SHORT).show();
                // Stuff to send report
                finish();
            }
        });

    }
}
