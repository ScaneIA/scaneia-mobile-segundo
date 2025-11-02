package com.example.scaneia.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.scaneia.Login;
import com.example.scaneia.model.*;
import com.example.scaneia.utils.JwtUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class ApiProxy {

    private final ScaneiaApiMongo mongoService;
    private final ScaneiaApiSQL sqlService;

    public ApiProxy() {
        mongoService = ApiClient.getMongoClient().create(ScaneiaApiMongo.class);
        sqlService = ApiClient.getSQLClient().create(ScaneiaApiSQL.class);
    }

    private <T> T executeWithTokenRefresh(Call<T> call) throws IOException {
        synchronized (ApiClient.getRefreshLock()) {
            if (JwtUtils.isExpired(ApiClient.getAccessToken())) {
                LoginResponse newTokens = ApiClient.refreshAccessToken();
                if (newTokens != null) {
                    ApiClient.setTokens(newTokens.getAccessToken(), newTokens.getRefreshToken());
                } else {
                    handleAuthFailure();
                    throw new IOException("Unable to refresh token");
                }
            } else {
                Log.i("TokenCheck", "Token not expired");
            }

            Log.i("AccessToken", ApiClient.getAccessToken());
            Log.i("RefreshToken", ApiClient.getRefreshToken());

            Call<T> authorizedCall = call.clone();
            return authorizedCall.execute().body();
        }
    }

    private void handleAuthFailure() {
        Context context = ApiClient.getAppContext();
        if (context != null) {
            ApiClient.clearTokens();
            Intent intent = new Intent(context, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } else {
            Log.e("ApiProxy", "No context available to redirect to login");
        }
    }

    // ------------------- Mongo API -------------------
    public List<FiltroInformacoesModelos> getFiltroInformacoesModelos() throws IOException {
        Call<List<FiltroInformacoesModelos>> call = mongoService.getFiltroInformacoesModelos();
        return executeWithTokenRefresh(call);
    }

    // ------------------- SQL API -------------------
    public LoginResponse login(LoginRequest request) throws IOException {
        Call<LoginResponse> call = sqlService.login(request);
        return call.execute().body();
    }

    public void logout(LogoutRequest request) throws IOException {
        Call<Void> call = sqlService.logout(request);
        executeWithTokenRefresh(call);
    }

    public UsuarioPerfilResponse getUsuarioPerfil() throws IOException {
        Call<UsuarioPerfilResponse> call = sqlService.getUsuarioPerfil();
        return executeWithTokenRefresh(call);
    }

    public String recuperarRoleUsuario(RefreshTokenRequestDTO request) throws IOException {
        Call<String> call = sqlService.recuperarRoleUsuario(request);
        return executeWithTokenRefresh(call);
    }

    public Map<String, Object> analyzeImage(String imageUrl) throws IOException {
        Map<String, String> body = Map.of("imageUrl", imageUrl);
        Call<Map<String, Object>> call = sqlService.analyzeImageByUrl(body);
        return executeWithTokenRefresh(call);
    }
}
