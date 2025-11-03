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
import retrofit2.Response;

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

    public List<UsuarioHierarquia> getUsuarioHierarquia(int id) throws IOException {
        Call<List<UsuarioHierarquia>> call = sqlService.getUsuarioHierarquia(id);
        return executeWithTokenRefresh(call);
    }

    public EstruturaResponse getEstruturaById(int id) throws IOException {
        Call<EstruturaResponse> call = sqlService.getEstruturaById(id);
        return executeWithTokenRefresh(call);
    }

    public int cadastrarUsuario(CadastroRequest request) throws IOException {
        Call<Void> call = sqlService.cadastrarUsuario(request);
        Response<Void> response = call.execute();
        return response.code();
    }

    public void createEscaneamento(String idModelo, List<List<String>> tableData) throws IOException {
        if (tableData == null || tableData.isEmpty()) {
            Log.e("ApiProxy", "Table data is empty!");
            return;
        }

        List<String> headers = tableData.get(0);
        List<Map<String, Object>> registros = new java.util.ArrayList<>();

        // Each subsequent row = data
        for (int i = 1; i < tableData.size(); i++) {
            List<String> row = tableData.get(i);
            Map<String, Object> registro = new java.util.HashMap<>();

            for (int j = 0; j < headers.size() && j < row.size(); j++) {
                registro.put(headers.get(j), row.get(j));
            }

            registros.add(registro);
        }

        Map<String, Object> body = new java.util.HashMap<>();
        body.put("idModelo", idModelo);
        body.put("registros", registros);

        Call<Void> call = mongoService.postEscaneamento(body);
        executeWithTokenRefresh(call);

        Log.i("ApiProxy", "✅ Escaneamento enviado com sucesso.");
    }
}
