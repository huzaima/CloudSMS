package co.magency.huzaima.cloudsms;

/**
 * Created by huzaima on 2/16/17.
 */

public class Sms {

    private String number;
    private String messsage;


    public Sms(String number, String messsage) {
        this.number = number;
        this.messsage = messsage;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMesssage() {
        return messsage;
    }

    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }
}
