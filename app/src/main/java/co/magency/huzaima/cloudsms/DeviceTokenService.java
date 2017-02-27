package co.magency.huzaima.cloudsms;

import com.google.firebase.iid.FirebaseInstanceIdService;

public class DeviceTokenService extends FirebaseInstanceIdService {
    public DeviceTokenService() {
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
    }
}
