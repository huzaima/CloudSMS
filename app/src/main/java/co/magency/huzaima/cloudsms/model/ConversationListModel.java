package co.magency.huzaima.cloudsms.model;

import java.util.ArrayList;

/**
 * Created by huzaima on 3/5/17.
 */

public class ConversationListModel {

    private ArrayList<Sms> list;
    private String email;

    public ConversationListModel() {
    }

    public ArrayList<Sms> getList() {
        return list;
    }

    public void setList(ArrayList<Sms> list) {
        this.list = list;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
