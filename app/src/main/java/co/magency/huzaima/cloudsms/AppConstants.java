package co.magency.huzaima.cloudsms;

/**
 * Created by huzaima on 3/18/17.
 */

public final class AppConstants {

    // Shared Preference Name
    public static final String SHARED_PREFERENCES_NAME = "sharedpref";

    // Shared Preferences Keys
    public static final String SHARED_PREF_IP_KEY = "sharedpref.key.ip";

    // Intent extra's keys
    public static final String AUTH_TOKEN = "intent.key.auth_token";

    // Socket emit events
    public static final String AUTH_TOKEN_EMIT = "androidAuth";
    public static final String FIRST_CONNECT_SYNC = "pushAllMsgData";
    public static final String NEW_MSG_RECEIVED = "newMsgRecv";
    public static final String MESSAGE_TO_SEND = "socket.on.message_to_send";

    // Notification id
    public static final int FOREGROUND_SERVICE_NOTIFICATION_ID = 1;
}