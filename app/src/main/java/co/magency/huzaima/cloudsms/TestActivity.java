package co.magency.huzaima.cloudsms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestActivity extends AppCompatActivity {

    String serverIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

//        TextView showMessage = (TextView) findViewById(R.id.show_text);

        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TestActivity.class.getSimpleName(), "OnClick");
                serverIP = ((EditText) findViewById(R.id.ip)).getText().toString();

                SharedPreferences preferences = getApplicationContext().getSharedPreferences("test", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ip", serverIP);
                editor.commit();
                ApiInterface apiInterface = ApiClient.getClient("http://" + serverIP).create(ApiInterface.class);
                Call<Ping> call = apiInterface.ping();
                call.enqueue(new Callback<Ping>() {
                    @Override
                    public void onResponse(Call<Ping> call, Response<Ping> response) {
                        Toast.makeText(TestActivity.this, "ping successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Ping> call, Throwable t) {
                        Toast.makeText(TestActivity.this, "ping failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("+923333523341", null, "Test", null, null);
            }
        });
    }
}