package co.magency.huzaima.cloudsms;

import android.app.IntentService;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.magency.huzaima.cloudsms.model.ConversationList;
import co.magency.huzaima.cloudsms.model.Sms;
import co.magency.huzaima.cloudsms.model.SmsWithNumber;

public class ConversationListService extends IntentService {

    private Map<String, Sms> allMessages;
    private String authToken;

    public ConversationListService() {
        super("ConversationListService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent == null || !intent.hasExtra(AppConstants.AUTH_TOKEN)) {
            stopSelf();
            return;
        }

        authToken = intent.getStringExtra(AppConstants.AUTH_TOKEN);

        allMessages = new HashMap<>();

        final Uri uriInbox = Uri.parse("content://sms/inbox");
        final Uri uriSent = Uri.parse("content://sms/sent");

        final String message[] = new String[1];
        final String[] col = {"body", "max(date)", "address"};

        // SELECT body, max(date) FROM sms GROUP BY address
        CursorLoader inboxLoader = new CursorLoader(getApplicationContext(), uriInbox, col, "1=1) GROUP BY (address", null, null);

        inboxLoader.registerListener(1, new Loader.OnLoadCompleteListener<Cursor>() {
            @Override
            public void onLoadComplete(Loader<Cursor> loader, final Cursor inboxCursor) {
                if (inboxCursor == null) {
                    return;
                }

                while (inboxCursor.moveToNext()) {
                    message[0] = inboxCursor.getString(0);
                    Date date = new Date();
                    date.setTime(Long.parseLong(inboxCursor.getString(1)));
                    Sms s = new Sms(message[0], date.getTime(), false);
                    allMessages.put(inboxCursor.getString(2), s);
                }
                inboxCursor.close();

                CursorLoader sentLoader = new CursorLoader(getApplicationContext(), uriSent, col, "1=1) GROUP BY (address", null, null);

                sentLoader.registerListener(2, new Loader.OnLoadCompleteListener<Cursor>() {
                    @Override
                    public void onLoadComplete(Loader<Cursor> loader, final Cursor sentCursor) {

                        if (sentCursor == null) {
                            return;
                        }

                        while (sentCursor.moveToNext()) {
                            String number = sentCursor.getString(2);
                            if (allMessages.containsKey(number)) {
                                Sms exist = allMessages.get(number);
                                Date date = new Date(Long.parseLong(sentCursor.getString(1)));
                                if (date.after(new Date(exist.getTimestamp()))) {
                                    allMessages.remove(number);
                                }
                            }

                            // Checking again because above if may remove key
                            if (!allMessages.containsKey(number)) {
                                message[0] = sentCursor.getString(0);
                                Date date = new Date(Long.parseLong(sentCursor.getString(1)));
                                Sms s = new Sms(message[0], date.getTime(), true);
                                allMessages.put(number, s);
                            }
                        }
                        sentCursor.close();
                        postList();
                    }
                });
                sentLoader.startLoading();
            }
        });
        inboxLoader.startLoading();
    }

    void postList() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(AppConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String ip = sharedPreferences.getString(AppConstants.SHARED_PREF_IP_KEY, null);

        List<SmsWithNumber> smsWithNumberList = new ArrayList<>(allMessages.keySet().size());

        for (String number : allMessages.keySet()) {
            Sms s = allMessages.get(number);
            smsWithNumberList.add(new SmsWithNumber(s.getMessage(), s.getTimestamp(), s.isSent(), number));
        }

        Collections.sort(smsWithNumberList);

        ConversationList<SmsWithNumber> conversationList = new ConversationList<>();
        conversationList.setConversations(smsWithNumberList);
        conversationList.setAuthToken(authToken);

        Type type = new TypeToken<ConversationList>() {
        }.getType();

        Gson gson = new Gson();
        String json = gson.toJson(conversationList, type);

        if (SocketHandler.socket != null)
            SocketHandler.socket.emit(AppConstants.FIRST_CONNECT_SYNC_EMIT, json);
    }
}