package co.magency.huzaima.cloudsms;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by huzaima on 2/16/17.
 */

public interface ApiInterface {

    @GET("/ping")
    Call<Ping> ping();

    @Headers({"Content-Type: application/json"})
    @POST("/sms")
    Call<Sms> sendSMS(@Body Sms sms);

    @Headers({"Content-Type: application/json"})
    @POST("/chat")
    Call<JSONObject> sendChatList(@Body JSONObject jsonObject);

    @Headers({"Content-Type: application/json"})
    @POST("/registerSocket")
    Call<JSONObject> registerSocket(@Body JSONObject jsonObject);
}
