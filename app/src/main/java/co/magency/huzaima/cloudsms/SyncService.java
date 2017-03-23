package co.magency.huzaima.cloudsms;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static co.magency.huzaima.cloudsms.SocketHandler.socket;

public class SyncService extends Service {
    public SyncService() {
        Log.v("lalala", "SyncSrvice constructor");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        Log.v("lalala", "In sync service");

        if (intent == null || !intent.hasExtra(AppConstants.AUTH_TOKEN)) {
            stopSelf();
        }

        try {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(AppConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
            String ip = sharedPreferences.getString(AppConstants.SHARED_PREF_IP_KEY, null);
            Log.v("lalala", "Trying to connect socket on ip: " + ip);
            socket = IO.socket(ip);

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.v("lalala", "Socket connected with id " + socket.id());
                    socket.emit(AppConstants.AUTH_TOKEN_EMIT, intent.getStringExtra(AppConstants.AUTH_TOKEN));
                    Intent firstTimeSync = new Intent(getApplicationContext(), ConversationListService.class);
                    firstTimeSync.putExtra(AppConstants.AUTH_TOKEN, "abc");
                    startService(firstTimeSync);
                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

//        Intent notificationIntent = new Intent(getApplicationContext(), TestActivity.class);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
//                0, notificationIntent, 0);
//
//        Notification notification = new NotificationCompat.Builder(getApplicationContext())
//                .setContentTitle("CloudSMS working")
//                .setContentText("Visit web page to see magic!")
//                .setOngoing(true)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        startForeground(AppConstants.FOREGROUND_SERVICE_NOTIFICATION_ID, notification);
        stopSelf();

        return START_REDELIVER_INTENT;
    }
}
