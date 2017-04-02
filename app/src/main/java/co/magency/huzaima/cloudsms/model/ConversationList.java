package co.magency.huzaima.cloudsms.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzaima on 3/19/17.
 */

public class ConversationList<T> {

    private String authToken;
    private List<T> conversations;
    private String number = null;

    public ConversationList() {
        conversations = new ArrayList<>();
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public List<T> getConversations() {
        return conversations;
    }

    public void setConversations(List<T> conversations) {
        this.conversations = conversations;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return authToken + " " + number + "\n\n" + conversations.toString();
    }
}
