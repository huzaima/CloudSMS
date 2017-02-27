package co.magency.huzaima.cloudsms;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class IncomingMessagingService extends FirebaseMessagingService {
    public IncomingMessagingService() {
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        Looper.prepare();
        Toast.makeText(getApplicationContext(), remoteMessage.getData().toString(), Toast.LENGTH_SHORT).show();
        Log.v("lalala", "lalala");
        Looper.loop();
    }
}
