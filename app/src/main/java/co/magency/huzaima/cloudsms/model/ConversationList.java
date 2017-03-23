package co.magency.huzaima.cloudsms.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huzaima on 3/19/17.
 */

public class ConversationList {

    private String authToken;
    private Map<String, List<Sms>> conversations;

    public ConversationList() {
        conversations = new HashMap<>();
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Map<String, List<Sms>> getConversations() {
        return conversations;
    }

    public void setConversations(Map<String, List<Sms>> conversations) {
        this.conversations = conversations;
    }
}
