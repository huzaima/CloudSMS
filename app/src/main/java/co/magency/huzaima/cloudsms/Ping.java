package co.magency.huzaima.cloudsms;

/**
 * Created by huzaima on 2/16/17.
 */

public class Ping {
    String message;

    public Ping(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
