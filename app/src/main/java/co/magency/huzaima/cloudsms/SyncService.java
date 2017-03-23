package co.magency.huzaima.cloudsms;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URISyntaxException;

import co.magency.huzaima.cloudsms.model.ReceivedSms;
import co.magency.huzaima.cloudsms.model.Sms;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static co.magency.huzaima.cloudsms.SocketHandler.socket;

public class SyncService extends Service {

    private String authToken;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        if (intent == null || !intent.hasExtra(AppConstants.AUTH_TOKEN)) {
            stopSelf();
        }

        authToken = intent.getStringExtra(AppConstants.AUTH_TOKEN);

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

        Intent notificationIntent = new Intent(getApplicationContext(), TestActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("CloudSMS working")
                .setContentText("Visit web page to see magic!")
                .setOngoing(false)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(AppConstants.FOREGROUND_SERVICE_NOTIFICATION_ID, notification);

        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");

        registerReceiver(incomingSmsReceiver, intentFilter);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(incomingSmsReceiver);
    }

    BroadcastReceiver incomingSmsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");

                if (socket == null) // socket is not connected
                    return;

                for (int i = 0; i < pdus.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    String number = currentMessage.getOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    long timestamp = currentMessage.getTimestampMillis();

                    ReceivedSms receivedSms = new ReceivedSms(new Sms(message, timestamp, false), number, authToken);

                    Type type = new TypeToken<ReceivedSms>() {
                    }.getType();

                    Gson gson = new Gson();
                    final String json = gson.toJson(receivedSms, type);
                    socket.emit(AppConstants.NEW_MSG_RECEIVED, json);
                }
            }
        }
    };
}
