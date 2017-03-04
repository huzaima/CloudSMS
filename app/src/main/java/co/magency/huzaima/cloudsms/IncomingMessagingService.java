package co.magency.huzaima.cloudsms;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class IncomingMessagingService extends FirebaseMessagingService {
    public IncomingMessagingService() {
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        Log.v("lalala", remoteMessage.getData().toString());
    }
}
