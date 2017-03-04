package co.magency.huzaima.cloudsms.model;

/**
 * Created by huzaima on 3/5/17.
 */

public class LoginModel {

    private String firebase_token;
    private String auth_token;
    private String email;

    public LoginModel() {

    }

    public String getFirebaseToken() {
        return firebase_token;
    }

    public void setFirebaseToken(String firebase_token) {
        this.firebase_token = firebase_token;
    }

    public String getAuthToken() {
        return auth_token;
    }

    public void setAuthToken(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
