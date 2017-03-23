package co.magency.huzaima.cloudsms.model;

/**
 * Created by huzaima on 3/23/17.
 */

public class ReceivedSms {

    private Sms sms;
    private String number, authToken;

    public ReceivedSms() {
    }

    public ReceivedSms(Sms sms, String number, String authToken) {
        this.sms = sms;
        this.number = number;
        this.authToken = authToken;
    }

    public Sms getSms() {
        return sms;
    }

    public void setSms(Sms sms) {
        this.sms = sms;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
