package co.magency.huzaima.cloudsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingSMSReceiver extends BroadcastReceiver {
    public IncomingSMSReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        SmsManager smsManager = SmsManager.getDefault();

        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");

            for (int i = 0; i < pdus.length; i++) {
                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String phone = currentMessage.getOriginatingAddress();
                String message = currentMessage.getDisplayMessageBody();

                Sms sms = new Sms(phone, message);

                SharedPreferences preferences = context.getSharedPreferences("test", Context.MODE_PRIVATE);
                String ip = preferences.getString("ip", null);
                if (ip == null) {
                    Toast.makeText(context, "IP is null in broadcast receiver. exiting...", Toast.LENGTH_LONG).show();
                    return;
                }

                ApiInterface apiInterface = ApiClient.getClient("http://" + ip).create(ApiInterface.class);
                Call<Sms> sendSMS = apiInterface.sendSMS(sms);
                sendSMS.enqueue(new Callback<Sms>() {
                    @Override
                    public void onResponse(Call<Sms> call, Response<Sms> response) {
                        Toast.makeText(context, "Sent", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Sms> call, Throwable t) {
                        Toast.makeText(context, "Unable to send", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.v(IncomingSMSReceiver.class.getSimpleName(), phone + ": " + message);
                Toast.makeText(context, phone + ": " + message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
