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

    public static final String BASE_URL = "http://188.235.162.32:28015/";
    private static Retrofit retrofit = null;
    private static Context appContext;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Логирование HTTP запросов
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                Log.d("RETROFIT", message);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Добавление токена в заголовки
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();

                        UserPrefs userPrefs = new UserPrefs(appContext);
                        String token = userPrefs.getToken();

                        if (token != null && !token.isEmpty()) {
                            Log.d("RETROFIT", "Добавляю токен в заголовок: " + token.substring(0, Math.min(20, token.length())) + "...");

                            Request request = original.newBuilder()
                                    .header("Authorization", "Bearer " + token)
                                    .method(original.method(), original.body())
                                    .build();
                            return chain.proceed(request);
                        } else {
                            Log.w("RETROFIT", "⚠️ Токен отсутствует!");
                        }

                        return chain.proceed(original);
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
