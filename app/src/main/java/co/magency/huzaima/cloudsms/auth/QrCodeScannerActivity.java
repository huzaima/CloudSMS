package co.magency.huzaima.cloudsms.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import co.magency.huzaima.cloudsms.ApiClient;
import co.magency.huzaima.cloudsms.ApiInterface;
import co.magency.huzaima.cloudsms.R;
import co.magency.huzaima.cloudsms.main.MainActivity;
import co.magency.huzaima.cloudsms.model.LoginModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QrCodeScannerActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView qrCodeReaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);

        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setAutofocusInterval(5000L);
        qrCodeReaderView.setBackCamera();

    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        qrCodeReaderView.stopCamera();
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        LoginModel loginModel = new LoginModel();
        loginModel.setAuthToken(text);
        loginModel.setFirebaseToken(FirebaseInstanceId.getInstance().getToken());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            loginModel.setEmail(user.getEmail());

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("cloudsms", MODE_PRIVATE);
        String ip = preferences.getString("ip", null);
        if (ip != null) {
            Toast.makeText(this, ip, Toast.LENGTH_SHORT).show();
            Log.v("lalala", loginModel.getEmail() + " " + loginModel.getAuthToken());
            ApiInterface apiInterface = ApiClient.getClient("http://" + ip).create(ApiInterface.class);
            Call<Void> login = apiInterface.login(loginModel);
            login.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(QrCodeScannerActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(QrCodeScannerActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }
}
