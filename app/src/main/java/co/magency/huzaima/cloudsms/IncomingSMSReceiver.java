package co.magency.huzaima.cloudsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class IncomingSMSReceiver extends BroadcastReceiver {
    public IncomingSMSReceiver() {
    }

    private CursorLoader root, inbox, sentLoader;
    private int counter = 0;

    @Override
    public void onReceive(final Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");

            for (int i = 0; i < pdus.length; i++) {
                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String phone = currentMessage.getOriginatingAddress();
                String message = currentMessage.getDisplayMessageBody();

//                Sms sms = new Sms(phone, message);

            }
        }
    }
}