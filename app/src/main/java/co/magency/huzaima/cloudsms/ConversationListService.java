package co.magency.huzaima.cloudsms;

import android.app.IntentService;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;

import co.magency.huzaima.cloudsms.model.ConversationListModel;
import co.magency.huzaima.cloudsms.model.Sms;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationListService extends IntentService {

    private CursorLoader root, inbox, sentLoader;
    private int counter = 1, total = 0;

    public ConversationListService() {
        super("ConversationListService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;
        final ArrayList<Sms> smsList = new ArrayList<>();

        final Uri uriSms = Uri.parse("content://sms/inbox");
        final Uri uriSentSms = Uri.parse("content://sms/sent");
        String[] columns = {"DISTINCT address"};

        root = new CursorLoader(getApplicationContext(), uriSms, columns, null, null, null);
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
                    inbox = new CursorLoader(getApplicationContext(), uriSms, col, where, whereArgs, "date DESC");
                    inbox.registerListener(2, new Loader.OnLoadCompleteListener<Cursor>() {
                        @Override
                        public void onLoadComplete(Loader<Cursor> loader, Cursor inboxCursor) {
                            final Sms received[] = {null}, sent[] = {null};
                            if (inboxCursor.moveToFirst()) {
                                message[0] = inboxCursor.getString(0);
                                Date date = new Date();
                                date.setTime(Long.parseLong(inboxCursor.getString(1)));
                                received[0] = new Sms(number[0], message[0], date.getTime());
                            }
                            inboxCursor.close();

                            sentLoader = new CursorLoader(getApplicationContext(), uriSentSms, col, where, whereArgs, "date DESC");
                            sentLoader.registerListener(3, new Loader.OnLoadCompleteListener<Cursor>() {
                                @Override
                                public void onLoadComplete(Loader<Cursor> loader, Cursor sentCursor) {
                                    if (sentCursor.moveToFirst()) {
                                        message[0] = sentCursor.getString(0);
                                        Date date = new Date();
                                        date.setTime(Long.parseLong(sentCursor.getString(1)));
                                        sent[0] = new Sms(number[0], message[0], date.getTime());
                                    }
                                    sentCursor.close();

                                    if (sent[0] == null) {
                                        Sms obj = new Sms();
                                        obj.setDate((new Date(received[0].getDate())).getTime());
                                        obj.setNumber(received[0].getNumber());
                                        obj.setMesssage(received[0].getMesssage());
                                        smsList.add(obj);
                                    } else if (received[0] == null) {
                                        Sms obj = new Sms();
                                        obj.setNumber(sent[0].getNumber());
                                        obj.setMesssage(sent[0].getMesssage());
                                        obj.setDate((new Date(sent[0].getDate())).getTime());
                                        smsList.add(obj);
                                    } else {
                                        Date dateSent = new Date(sent[0].getDate());
                                        Date dateReceived = new Date(received[0].getDate());

                                        if (dateSent.after(dateReceived)) {
                                            Sms obj = new Sms();
                                            obj.setNumber(sent[0].getNumber());
                                            obj.setMesssage(sent[0].getMesssage());
                                            obj.setDate((new Date(sent[0].getDate())).getTime());
                                            smsList.add(obj);
                                        } else {
                                            Sms obj = new Sms();
                                            obj.setDate((new Date(received[0].getDate())).getTime());
                                            obj.setNumber(received[0].getNumber());
                                            obj.setMesssage(received[0].getMesssage());
                                            smsList.add(obj);
                                        }
                                    }

                                    if (++counter == total) {
                                        postList(smsList);
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

    void postList(ArrayList<Sms> smsList) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("cloudsms", MODE_PRIVATE);
        String ip = sharedPreferences.getString("ip", null);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ConversationListModel conversationListModel = null;
        if (user != null) {
            conversationListModel = new ConversationListModel();
            conversationListModel.setList(smsList);
            conversationListModel.setEmail(user.getEmail());
            ApiInterface apiInterface = ApiClient.getClient("http://" + ip).create(ApiInterface.class);
            Call<Void> postConversationList = apiInterface.sendChatList(conversationListModel);

            postConversationList.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.v("lalala", "conversationList posted successfully");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.v("lalala", "failed to post conversationList");
                }
            });
        }
    }
}
