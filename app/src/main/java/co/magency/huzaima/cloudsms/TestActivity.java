package co.magency.huzaima.cloudsms;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;

import java.io.IOException;

public class TestActivity extends AppCompatActivity {

    final String LOG_TAG = TestActivity.class.getSimpleName();
    String serverIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TestActivity.class.getSimpleName(), "OnClick");
                serverIP = ((EditText) findViewById(R.id.ip)).getText().toString();
                SharedPreferences.Editor editor = getSharedPreferences("cloudsms", MODE_PRIVATE).edit();
                editor.putString("ip", serverIP);
                editor.commit();
            }
        });

        findViewById(R.id.send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SmsDb.readSMS(TestActivity.this, serverIP);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}