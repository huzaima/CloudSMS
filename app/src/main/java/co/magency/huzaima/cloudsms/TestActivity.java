package co.magency.huzaima.cloudsms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TestActivity extends AppCompatActivity {

    private final String LOG_TAG = TestActivity.class.getSimpleName();
    private String serverIP;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        editText = (EditText) findViewById(R.id.ip);

        SharedPreferences preferences = getApplicationContext()
                .getSharedPreferences(AppConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        editText.setText(preferences.getString(AppConstants.SHARED_PREF_IP_KEY, "http://192.168.1.x:3000"));

        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "OnClick");
                serverIP = editText.getText().toString().trim();
                SharedPreferences.Editor editor = getSharedPreferences(AppConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit();
                editor.putString(AppConstants.SHARED_PREF_IP_KEY, serverIP);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), SyncService.class);
                intent.putExtra(AppConstants.AUTH_TOKEN, "abc");
                startService(intent);
                Toast.makeText(TestActivity.this, "SMS sync started", Toast.LENGTH_SHORT).show();
            }
        });
    }

}