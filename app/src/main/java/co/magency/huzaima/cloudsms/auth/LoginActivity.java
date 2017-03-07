package co.magency.huzaima.cloudsms.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import co.magency.huzaima.cloudsms.R;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView email, password;
    private Button login;
    private Socket socket;
    //    private IntentIntegrator qrScanner;
    private final String LOG_TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        qrScanner = new IntentIntegrator(this);
//        Log.v(LOG_TAG, FirebaseInstanceId.getInstance().getToken());

        email = (TextView) findViewById(R.id.email);
        password = (TextView) findViewById(R.id.password);

        login = (Button) findViewById(R.id.login);

        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        mAuth.signInWithEmailAndPassword(email.getText().toString().trim(),
//                password.getText().toString().trim())
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (!task.isSuccessful())
//                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
//                        else {
//                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(getApplicationContext(), QrCodeScannerActivity.class));
////                            qrScanner.initiateScan();
//                        }
//                    }
//                });

        try {
            socket = IO.socket("http://192.168.1.2:3000");
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                        }
                    });
                    try {
                        socket.emit("abc", (new JSONObject()).put("abc", "abc").put("id", socket.id()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Disconnect", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            socket.on("abc", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "abc called", Toast.LENGTH_SHORT).show();
                            Log.v("lalala", "abc called");
                        }
                    });
                }
            });

            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//
//        if (result != null) {
//            if (result.getContents() == null)
//                Toast.makeText(this, "Empty QR code", Toast.LENGTH_SHORT).show();
//            else {
//                Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
    }
}
