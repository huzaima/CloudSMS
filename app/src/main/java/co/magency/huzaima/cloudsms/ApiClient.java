package co.magency.huzaima.cloudsms;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huzaima on 2/16/17.
 */

public class ApiClient {

    private static Retrofit retrofit;
    private static String baseURL = "";

    public static Retrofit getClient(final String BASE_URL) {
        if (retrofit == null || !baseURL.matches(BASE_URL)) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
