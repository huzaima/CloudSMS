package co.magency.huzaima.cloudsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import co.magency.huzaima.cloudsms.model.Sms;

public class IncomingSMSReceiver extends BroadcastReceiver {
    public IncomingSMSReceiver() {
    }

    private CursorLoader root, inbox, sentLoader;
    private int counter = 0;

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            final JSONArray smsList = new JSONArray();
            final Uri uriSms = Uri.parse("content://sms/inbox");
            final Uri uriSentSms = Uri.parse("content://sms/sent");
            String[] columns = {"DISTINCT address"};

            final CursorLoader root = new CursorLoader(context, uriSms, columns, null, null, null);
            root.registerListener(1, new Loader.OnLoadCompleteListener<Cursor>() {
                @Override
                public void onLoadComplete(Loader<Cursor> loader, Cursor rootCursor) {

                    if (rootCursor.moveToFirst()) {
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
                            inbox = new CursorLoader(context, uriSms, col, where, whereArgs, "date DESC");
                            inbox.registerListener(2, new Loader.OnLoadCompleteListener<Cursor>() {
                                @Override
                                public void onLoadComplete(Loader<Cursor> loader, Cursor inboxCursor) {

                                    final Sms received[] = {null}, sent[] = {null};
                                    if (inboxCursor.moveToFirst()) {
                                        message[0] = inboxCursor.getString(0);
                                        Date date = new Date();
                                        date.setTime(Long.parseLong(inboxCursor.getString(1)));
                                        received[0] = new Sms(number[0], message[0], date.toString());
//                    Log.v("lalala", received.toString());
                                    }
                                    inboxCursor.close();

                                    sentLoader = new CursorLoader(context, uriSentSms, col, where, whereArgs, "date DESC");
                                    sentLoader.registerListener(3, new Loader.OnLoadCompleteListener<Cursor>() {
                                        @Override
                                        public void onLoadComplete(Loader<Cursor> loader, Cursor sentCursor) {

                                            if (sentCursor.moveToFirst()) {
                                                message[0] = sentCursor.getString(0);
                                                Date date = new Date();
                                                date.setTime(Long.parseLong(sentCursor.getString(1)));
                                                sent[0] = new Sms(number[0], message[0], date.toString());
                                            }
                                            sentCursor.close();

                                            try {
                                                if (sent[0] == null) {
                                                    JSONObject obj = new JSONObject();
                                                    obj.put("number", received[0].getNumber());
                                                    obj.put("message", received[0].getMesssage());
                                                    obj.put("timestamp", (new Date(received[0].getDate())).getTime());
                                                    smsList.put(obj);
                                                } else if (received[0] == null) {
                                                    JSONObject obj = new JSONObject();
                                                    obj.put("number", sent[0].getNumber());
                                                    obj.put("message", sent[0].getMesssage());
                                                    obj.put("timestamp", (new Date(received[0].getDate())).getTime());
                                                    smsList.put(obj);
                                                } else {
                                                    Date dateSent = new Date(sent[0].getDate());
                                                    Date dateReceived = new Date(received[0].getDate());

                                                    if (dateSent.after(dateReceived)) {
                                                        JSONObject obj = new JSONObject();
                                                        obj.put("number", sent[0].getNumber());
                                                        obj.put("message", sent[0].getMesssage());
                                                        obj.put("timestamp", (new Date(received[0].getDate())).getTime());
                                                        smsList.put(obj);
                                                    } else {
                                                        JSONObject obj = new JSONObject();
                                                        obj.put("number", received[0].getNumber());
                                                        obj.put("message", received[0].getMesssage());
                                                        obj.put("timestamp", (new Date(received[0].getDate())).getTime());
                                                        smsList.put(obj);
                                                    }
                                                }


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    sentLoader.startLoading();
                                }
                            });

                            inbox.startLoading();
                        }

                    }
                    counter++;
                    if (counter == 2) {
                        Log.v("lalala", "lalala" + counter);
                    }
                }

            });
            root.startLoading();
        }

//        if (bundle != null) {
//            Object[] pdus = (Object[]) bundle.get("pdus");
//
//            for (int i = 0; i < pdus.length; i++) {
//                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                String phone = currentMessage.getOriginatingAddress();
//                String message = currentMessage.getDisplayMessageBody();
//
//                Sms sms = new Sms(phone, message);
//
//                SharedPreferences preferences = context.getSharedPreferences("test", Context.MODE_PRIVATE);
//                String ip = preferences.getString("ip", null);
//                if (ip == null) {
//                    Toast.makeText(context, "IP is null in broadcast receiver. exiting...", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                ApiInterface apiInterface = ApiClient.getClient("http://" + ip).create(ApiInterface.class);
//                Call<Sms> sendSMS = apiInterface.sendSMS(sms);
//                sendSMS.enqueue(new Callback<Sms>() {
//                    @Override
//                    public void onResponse(Call<Sms> call, Response<Sms> response) {
//                        Toast.makeText(context, "Sent", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailure(Call<Sms> call, Throwable t) {
//                        Toast.makeText(context, "Unable to send", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//                Log.v(IncomingSMSReceiver.class.getSimpleName(), phone + ": " + message);
//                Toast.makeText(context, phone + ": " + message, Toast.LENGTH_SHORT).show();
//            }
//        }
    }
}
