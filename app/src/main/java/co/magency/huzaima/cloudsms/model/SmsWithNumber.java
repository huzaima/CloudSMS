package co.magency.huzaima.cloudsms.model;

/**
 * Created by huzaima on 3/25/17.
 */

public class SmsWithNumber extends Sms {

    private String number;

    public SmsWithNumber(String message, long timestamp, boolean sent, String number) {
        super(message, timestamp, sent);
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
