package co.magency.huzaima.cloudsms;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class DeviceTokenService extends FirebaseInstanceIdService {
    public DeviceTokenService() {
    }

    @Override
    public void onTokenRefresh() {
        Log.v("lalala", FirebaseInstanceId.getInstance().getToken());
    }
}
