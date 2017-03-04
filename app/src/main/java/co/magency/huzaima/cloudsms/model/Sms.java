package co.magency.huzaima.cloudsms.model;

/**
 * Created by huzaima on 2/16/17.
 */

public class Sms {

    private String number;
    private String messsage;
    private String date;

    public Sms() {
    }

    public Sms(String number, String messsage) {
        this.number = number;
        this.messsage = messsage;
    }

    public Sms(String number, String messsage, String date) {
        this.number = number;
        this.messsage = messsage;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return date + ": " + number + ": " + messsage + "\n-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-\n\n\n";
    }
}
