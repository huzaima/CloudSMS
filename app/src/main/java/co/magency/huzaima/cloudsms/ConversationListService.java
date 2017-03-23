package co.magency.huzaima.cloudsms;

import android.app.IntentService;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import co.magency.huzaima.cloudsms.model.ConversationList;
import co.magency.huzaima.cloudsms.model.Sms;

public class ConversationListService extends IntentService {

    private CursorLoader root, inbox, sentLoader;
    private int counter = 1, total = 0;
    private ConversationList conversationList;
    private String authToken;

    public ConversationListService() {
        super("ConversationListService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent == null || !intent.hasExtra(AppConstants.AUTH_TOKEN))
            return;

        authToken = intent.getStringExtra(AppConstants.AUTH_TOKEN);

        conversationList = new ConversationList();

        final Uri uriInbox = Uri.parse("content://sms/inbox");
        final Uri uriSent = Uri.parse("content://sms/sent");
        String[] columns = {"DISTINCT address"};

        root = new CursorLoader(getApplicationContext(), uriInbox, columns, null, null, null);
        root.registerListener(1, new Loader.OnLoadCompleteListener<Cursor>() {
            @Override
            public void onLoadComplete(Loader<Cursor> loader, final Cursor rootCursor) {

                if (!rootCursor.moveToFirst())
                    return;

                total = rootCursor.getCount();

                while (rootCursor.moveToNext()) {
//                Log.v("lalala", cursor.getString(cursor.getColumnIndexOrThrow("_id")));
//                Log.v("lalala", cursor.getString(cursor.getColumnIndexOrThrow("body")));
//                Log.v("lalala", cursor.getString(cursor.getColumnIndex("read")));
//                Log.v("lalala", cursor.getString(cursor.getColumnIndexOrThrow("date")));
                    final String number[] = {rootCursor.getString(rootCursor.getColumnIndexOrThrow("address"))};
                    final String message[] = new String[1];
                    final String[] col = {"body", "date"};
                    final String where = "address = ?";
                    final String[] whereArgs = {number[0]};

                    if (!conversationList.getConversations().containsKey(number[0])) {
                        conversationList.getConversations().put(number[0], new LinkedList<Sms>());
                    }

                    // Querying inbox messages
                    inbox = new CursorLoader(getApplicationContext(), uriInbox, col, where, whereArgs, "date DESC");
                    inbox.registerListener(2, new Loader.OnLoadCompleteListener<Cursor>() {
                        @Override
                        public void onLoadComplete(Loader<Cursor> loader, Cursor inboxCursor) {

                            if (inboxCursor.moveToFirst()) {
                                List<Sms> received = new ArrayList<>(inboxCursor.getCount());
                                do {
                                    message[0] = inboxCursor.getString(0);
                                    Date date = new Date();
                                    date.setTime(Long.parseLong(inboxCursor.getString(1)));
                                    received.add(new Sms(message[0], date.getTime(), false));
                                } while (inboxCursor.moveToNext());
                                conversationList.getConversations().get(number[0]).addAll(received);
                            }
                            inboxCursor.close();

                            // Querying sent messages
                            sentLoader = new CursorLoader(getApplicationContext(), uriSent, col, where, whereArgs, "date DESC");
                            sentLoader.registerListener(3, new Loader.OnLoadCompleteListener<Cursor>() {
                                @Override
                                public void onLoadComplete(Loader<Cursor> loader, Cursor sentCursor) {

                                    if (sentCursor.moveToFirst()) {
                                        List<Sms> sent = new ArrayList<>(sentCursor.getCount());
                                        do {
                                            message[0] = sentCursor.getString(0);
                                            Date date = new Date();
                                            date.setTime(Long.parseLong(sentCursor.getString(1)));
                                            sent.add(new Sms(message[0], date.getTime(), true));
                                        } while (sentCursor.moveToNext());
                                        conversationList.getConversations().get(number[0]).addAll(sent);
                                    }
                                    sentCursor.close();

                                    Collections.sort(conversationList.getConversations().get(number[0]));

                                    if (++counter == total) {
                                        postList();
                                    }
                                }
                            });
                            sentLoader.startLoading();
                        }
                    });
                    inbox.startLoading();
                }
            }
        });
        root.startLoading();
    }


    void postList() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(AppConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String ip = sharedPreferences.getString(AppConstants.SHARED_PREF_IP_KEY, null);

        Type type = new TypeToken<ConversationList>() {
        }.getType();

        Gson gson = new Gson();
        conversationList.setAuthToken(authToken);
        final String json = gson.toJson(conversationList, type);
        if (SocketHandler.socket != null)
            SocketHandler.socket.emit(AppConstants.FIRST_CONNECT_SYNC, json);
        Log.v("lalala", json);
    }
}