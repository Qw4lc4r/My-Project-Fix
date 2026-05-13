package com.example.csharpmanualv2;

import android.app.Application;
import android.util.Log;

import com.example.csharpmanualv2.network.RetrofitClient;



public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("APPpopa", "MyApplication onCreate");

        RetrofitClient.init(this);
        Log.d("APPpopa", "RetrofitClient инициализирован");
    }
}
