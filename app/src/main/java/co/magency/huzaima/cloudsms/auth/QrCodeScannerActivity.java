package co.magency.huzaima.cloudsms.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import java.net.URISyntaxException;

import co.magency.huzaima.cloudsms.AppConstants;
import co.magency.huzaima.cloudsms.R;
import co.magency.huzaima.cloudsms.SyncService;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

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
    public void onQRCodeRead(final String text, PointF[] points) {

        SharedPreferences preferences = getApplicationContext()
                .getSharedPreferences(AppConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        final String ip = preferences.getString(AppConstants.SHARED_PREF_IP_KEY, null);

        qrCodeReaderView.stopCamera();
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

        if (ip != null) {
            // TODO: POST auth token (String text) to server

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setIndeterminate(true);
            pd.setTitle("Authenticating");
            pd.setMessage("Please wait...");
            pd.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Socket socket = IO.socket(ip);

                        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                socket.emit(AppConstants.AUTH_TOKEN_EMIT, text);

                                Intent intent = new Intent(getApplicationContext(), SyncService.class);
                                intent.putExtra(AppConstants.AUTH_TOKEN, text);
                                stopService(intent);
                                startService(intent);
                                socket.disconnect();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        QrCodeScannerActivity.this.finish();
                                        Toast.makeText(QrCodeScannerActivity.this, "All set!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        socket.connect();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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
