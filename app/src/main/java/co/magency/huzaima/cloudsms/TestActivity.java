package co.magency.huzaima.cloudsms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import co.magency.huzaima.cloudsms.auth.LoginActivity;

public class TestActivity extends AppCompatActivity {

    final String LOG_TAG = TestActivity.class.getSimpleName();
    private String serverIP;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        editText = (EditText) findViewById(R.id.ip);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("cloudsms", MODE_PRIVATE);
        editText.setText(preferences.getString("ip", "192.168.1.x:3000"));

        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TestActivity.class.getSimpleName(), "OnClick");
                serverIP = editText.getText().toString();
                SharedPreferences.Editor editor = getSharedPreferences("cloudsms", MODE_PRIVATE).edit();
                editor.putString("ip", serverIP);
                editor.commit();

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

}