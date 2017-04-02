package co.magency.huzaima.cloudsms.model;

/**
 * Created by huzaima on 3/23/17.
 */

public class ReceivedSms extends SmsWithNumber {

    private String authToken;

    public ReceivedSms(String message, long timestamp, boolean sent, String number, String authToken) {
        super(message, timestamp, sent, number);
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
