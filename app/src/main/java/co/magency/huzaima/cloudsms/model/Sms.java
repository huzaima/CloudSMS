package co.magency.huzaima.cloudsms.model;

import android.support.annotation.NonNull;

/**
 * Created by huzaima on 3/19/17.
 */

public class Sms implements Comparable<Sms> {

    private String message;
    private long timestamp;
    private boolean sent;

    public Sms(String message, long timestamp, boolean sent) {
        this.message = message;
        this.timestamp = timestamp;
        this.sent = sent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    @Override
    public int compareTo(@NonNull Sms o) {

        if (this.timestamp < o.getTimestamp())
            return 1;
        else if (this.timestamp > o.getTimestamp())
            return -1;
        else
            return 0;
    }
}
