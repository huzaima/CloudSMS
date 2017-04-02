package co.magency.huzaima.cloudsms;

import android.app.IntentService;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import co.magency.huzaima.cloudsms.model.ConversationList;
import co.magency.huzaima.cloudsms.model.Sms;

public class ChatDetailService extends IntentService {

    String number, authToken;
    List<Sms> smses;

    public ChatDetailService() {
        super("ChatDetailService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        try {
            Thread.sleep(2500); // let the message be put in database
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        number = intent.getStringExtra(AppConstants.GET_CHAT_FOR_NUMBER);
        authToken = intent.getStringExtra(AppConstants.AUTH_TOKEN);
        smses = new ArrayList<>();

        final Uri uriInbox = Uri.parse("content://sms/inbox");
        final Uri uriSent = Uri.parse("content://sms/sent");

        final String message[] = new String[1];
        final String[] col = {"body", "date"};
        final String where = "address = ?";
        final String[] whereArgs = {number};

        // SELECT body, max(date) FROM table GROUP BY address HAVING date==max(date);
        CursorLoader inboxLoader = new CursorLoader(getApplicationContext(), uriInbox, col, where, whereArgs, null);

        inboxLoader.registerListener(10, new Loader.OnLoadCompleteListener<Cursor>() {
            @Override
            public void onLoadComplete(Loader<Cursor> loader, final Cursor inboxCursor) {

                if (inboxCursor == null) {
                    return;
                }

                while (inboxCursor.moveToNext()) {
                    message[0] = inboxCursor.getString(0);
                    Date date = new Date(Long.parseLong(inboxCursor.getString(1)));
                    smses.add(new Sms(message[0], date.getTime(), false));
                }
                inboxCursor.close();

                CursorLoader sentLoader = new CursorLoader(getApplicationContext(), uriSent, col, where, whereArgs, null);

                sentLoader.registerListener(2, new Loader.OnLoadCompleteListener<Cursor>() {
                    @Override
                    public void onLoadComplete(Loader<Cursor> loader, final Cursor sentCursor) {

                        if (sentCursor == null) {
                            return;
                        }
                        while (sentCursor.moveToNext()) {
                            message[0] = sentCursor.getString(0);
                            Date date = new Date(Long.parseLong(sentCursor.getString(1)));
                            smses.add(new Sms(message[0], date.getTime(), true));
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

    public void postList() {

        if (SocketHandler.socket == null) {
            return;
        }

        Collections.sort(smses);
        ConversationList<Sms> conversationList = new ConversationList<>();
        conversationList.setConversations(smses);
        conversationList.setAuthToken(authToken);
        conversationList.setNumber(number);

        Type type = new TypeToken<ConversationList>() {
        }.getType();

        Gson gson = new Gson();
        String json = gson.toJson(conversationList, type);
        Log.v("lalala", json);

        SocketHandler.socket.emit(AppConstants.CHAT_DETAIL_EMIT, json);
        stopSelf();
    }
}