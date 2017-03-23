package co.magency.huzaima.cloudsms.auth;

import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import co.magency.huzaima.cloudsms.AppConstants;
import co.magency.huzaima.cloudsms.R;

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

        SharedPreferences preferences = getApplicationContext()
                .getSharedPreferences(AppConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String ip = preferences.getString(AppConstants.SHARED_PREF_IP_KEY, null);

        if (ip != null) {
            // TODO: POST auth token (String text) to server
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
