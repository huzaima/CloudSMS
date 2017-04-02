package co.magency.huzaima.cloudsms;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static co.magency.huzaima.cloudsms.SocketHandler.socket;

public class SyncService extends Service {

    private String authToken;

    @Override
    public IBinder onBind(Intent intent) {
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
                    firstTimeSync.putExtra(AppConstants.AUTH_TOKEN, authToken);
                    startService(firstTimeSync);
                }
            });

            socket.on(AppConstants.GET_CHAT_EVENT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String number = (String) args[0];
                    Log.v("lalala", "Received request for number: " + number);
                    Intent getChatIntent = new Intent(getApplicationContext(), ChatDetailService.class);
                    getChatIntent.putExtra(AppConstants.GET_CHAT_FOR_NUMBER, number);
                    getChatIntent.putExtra(AppConstants.AUTH_TOKEN, authToken);
                    stopService(getChatIntent);
                    startService(getChatIntent);
                }
            });

            socket.on(AppConstants.GET_ALL_CHAT_HEADS, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.v("lalala", "Received request for getting all chat heads");
                    Intent syncList = new Intent(getApplicationContext(), ConversationListService.class);
                    syncList.putExtra(AppConstants.AUTH_TOKEN, authToken);
                    startService(syncList);
                }
            });

            socket.on(AppConstants.SEND_NEW_MESSAGE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];
                        Log.v("lalala", "Received: " + jsonObject.toString());
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(jsonObject.getString("number"), null,
                                jsonObject.getString("message"), null, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Intent notificationIntent = new Intent(getApplicationContext(), TestActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                164, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(getApplicationContext()).build();

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

            if (socket == null) // socket is not connected
                return;

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("authToken", authToken);
                Log.v("lalala", "Emitting: " + jsonObject.toString());
                socket.emit(AppConstants.NEW_MSG_RECEIVED, jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
