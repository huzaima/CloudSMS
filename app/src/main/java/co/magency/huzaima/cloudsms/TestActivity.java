package co.magency.huzaima.cloudsms;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import co.magency.huzaima.cloudsms.auth.QrCodeScannerActivity;

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
//        editText.setText("http://ec2-52-90-238-236.compute-1.amazonaws.com:3000");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            takePermissions();

        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "OnClick");
                serverIP = editText.getText().toString().trim();
                SharedPreferences.Editor editor = getSharedPreferences(AppConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit();
                editor.putString(AppConstants.SHARED_PREF_IP_KEY, serverIP);
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), QrCodeScannerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void takePermissions() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    AppConstants.GRANT_PERMISSION);
        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    AppConstants.GRANT_PERMISSION);
        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    AppConstants.GRANT_PERMISSION);
        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    AppConstants.GRANT_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case AppConstants.GRANT_PERMISSION:
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        takePermissions();
                        return;
                    }
                }
                break;
        }
    }
}