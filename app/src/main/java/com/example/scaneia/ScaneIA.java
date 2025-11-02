package com.example.scaneia;

import android.app.Application;
import com.example.scaneia.api.ApiClient;

public class ScaneIA extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApiClient.init(getApplicationContext());
    }
}