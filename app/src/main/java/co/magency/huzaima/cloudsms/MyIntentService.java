package co.magency.huzaima.cloudsms;

import android.app.IntentService;
import android.content.CursorLoader;
import android.content.Intent;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {

    public MyIntentService() {
        super("MyIntentService");
    }

    private CursorLoader root, inbox, sentLoader;


    @Override
    protected void onHandleIntent(Intent intent) {


    }

}