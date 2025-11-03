package com.example.scaneia.api;

import android.content.Context;

import com.example.scaneia.model.LoginResponse;
import com.example.scaneia.model.RefreshTokenRequestDTO;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static String accessToken;
    private static String refreshToken;
    private static TokenManager tokenManager;
    private static final Object refreshLock = new Object();

    private static Retrofit mongoRetrofit;
    private static Retrofit sqlRetrofit;

    private static final String MONGO_BASE_URL = "https://scaneia-apids2-mongodb.onrender.com/";
    private static final String SQL_BASE_URL = "https://scaneia-apids2-segundo.onrender.com/";
    private static Context appContext;

    public static void init(Context context) {
        tokenManager = new TokenManager(context);
        accessToken = tokenManager.getAccessToken();
        refreshToken = tokenManager.getRefreshToken();
        appContext = context.getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static synchronized void setTokens(String newAccessToken, String newRefreshToken) {
        accessToken = newAccessToken;
        refreshToken = newRefreshToken;
        tokenManager.saveTokens(newAccessToken, newRefreshToken);
    }

    public static String getAccessToken() { return accessToken; }
    public static String getRefreshToken() { return refreshToken; }
    public static Object getRefreshLock() { return refreshLock; }

    // -------------------- Retrofit Instances --------------------
    public static Retrofit getMongoClient() {
        if (mongoRetrofit == null) {
            mongoRetrofit = buildRetrofit(MONGO_BASE_URL);
        }
        return mongoRetrofit;
    }

    public static Retrofit getSQLClient() {
        if (sqlRetrofit == null) {
            sqlRetrofit = buildRetrofit(SQL_BASE_URL);
        }
        return sqlRetrofit;
    }

    private static Retrofit buildRetrofit(String baseUrl) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    okhttp3.Request request = chain.request().newBuilder()
                            .header("Authorization", "Bearer " + accessToken)
                            .build();
                    return chain.proceed(request);
                })
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // -------------------- Token Refresh --------------------
    public static LoginResponse refreshAccessToken() {
        synchronized (refreshLock) {
            try {
                // Use a basic OkHttpClient without interceptor
                OkHttpClient client = new OkHttpClient.Builder().build();
                Retrofit refreshRetrofit = new Retrofit.Builder()
                        .baseUrl(SQL_BASE_URL) // or whichever endpoint handles refresh
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ScaneiaApiSQL service = refreshRetrofit.create(ScaneiaApiSQL.class);
                RefreshTokenRequestDTO body = new RefreshTokenRequestDTO(refreshToken);
                Call<LoginResponse> call = service.refresh(body); // or a dedicated refresh endpoint
                retrofit2.Response<LoginResponse> resp = call.execute();

                if (resp.isSuccessful() && resp.body() != null) {
                    setTokens(resp.body().getAccessToken(), resp.body().getRefreshToken());
                    return resp.body();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static void clearTokens() {
        accessToken = "";
        refreshToken = "";
        tokenManager.saveTokens("", "");
    }
}
