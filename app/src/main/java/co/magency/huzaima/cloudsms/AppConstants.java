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
    public static final String GET_CHAT_FOR_NUMBER = "intent.key.chat_for_number";

    // Socket events
    public static final String AUTH_TOKEN_EMIT = "androidAuth";
    public static final String FIRST_CONNECT_SYNC_EMIT = "pushAllMsgData";
    public static final String CHAT_DETAIL_EMIT = "chatForNumber";
    public static final String NEW_MSG_RECEIVED = "newMsgRecv";
    public static final String GET_CHAT_EVENT = "getChat";
    public static final String GET_ALL_CHAT_HEADS = "sendChatHeads";
    public static final String SEND_NEW_MESSAGE = "sendMsg";

    // Notification id
    public static final int FOREGROUND_SERVICE_NOTIFICATION_ID = 1;

    // Runtime Permissions
    public static final int GRANT_PERMISSION = 1;
}