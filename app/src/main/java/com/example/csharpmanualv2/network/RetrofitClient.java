package com.example.csharpmanualv2.network;

import android.content.Context;
import android.util.Log;

import com.example.csharpmanualv2.SharedPreferences.UserPrefs;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // ЗАМЕНИ на свои данные из Supabase (Settings -> API)
    public static final String BASE_URL = "https://vdmksxsiigqqfpwmuzct.supabase.co/";
    public static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZkbWtzeHNpaWdxcWZwd211emN0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzg2ODk2NDUsImV4cCI6MjA5NDI2NTY0NX0.JO5p1oHZFysvICqH_kyZRvRR3-_A4T8thF1-rTa2Fr4";

    private static Retrofit retrofit = null;
    private static Context appContext;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                Log.d("RETROFIT", message);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        UserPrefs userPrefs = new UserPrefs(appContext);
                        String token = userPrefs.getToken();

                        Request.Builder requestBuilder = original.newBuilder()
                                .header("apikey", SUPABASE_KEY)
                                .header("Content-Type", "application/json");

                        // Если есть токен пользователя (JWT), используем его, иначе anon key
                        if (token != null && !token.isEmpty()) {
                            requestBuilder.header("Authorization", "Bearer " + token);
                        } else {
                            requestBuilder.header("Authorization", "Bearer " + SUPABASE_KEY);
                        }

                        return chain.proceed(requestBuilder.build());
                    })
                    .addInterceptor(loggingInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApi() {
        return getClient().create(ApiService.class);
    }
}