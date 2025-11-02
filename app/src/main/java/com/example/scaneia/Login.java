package com.example.scaneia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.scaneia.api.ScaneiaApiSQL;
import com.example.scaneia.api.RetrofitClient;
import com.example.scaneia.model.LoginRequest;
import com.example.scaneia.model.LoginResponse;
import com.example.scaneia.utils.DialogsUtils;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private TextInputEditText editEmail, editSenha;
    private Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editEmail = findViewById(R.id.username);
        editSenha = findViewById(R.id.password);
        btnEntrar = findViewById(R.id.sair);

        // Verifica se já existe token salvo
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = prefs.getString("refresh_token", null);
        if (token != null) {
            // já logado → vai direto pra HomeActivity
            startActivity(new Intent(Login.this, SplashScrenn.class));
            finish();
            return;
        }

        btnEntrar.setOnClickListener(v -> fazerLogin());

        //Cadastro
        TextView btnCadastrar = findViewById(R.id.cadastro);
        btnCadastrar.setOnClickListener( v ->
                startActivity(new Intent(Login.this, PrimeiroAcesso.class))
        );
    }
    private void fazerLogin() {
        String username = editEmail.getText().toString().trim();
        String password = editSenha.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            DialogsUtils.mostrarToast(Login.this, "Preencha todos os campos", false);
            return;
        }

        ScaneiaApiSQL scaneiaApi = RetrofitClient.getClientSQL().create(ScaneiaApiSQL.class);
        LoginRequest request = new LoginRequest(username, password);

        Call<LoginResponse> call = scaneiaApi.login(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String refreshToken = response.body().getRefreshToken();
                    String accessToken = response.body().getAccessToken();

                    // salva token localmente
                    SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    prefs.edit().putString("refresh_token", refreshToken).apply();
                    prefs.edit().putString("access_token", accessToken).apply();
                    DialogsUtils.mostrarToast(Login.this, "Login realizado com sucesso", true);

                    // vai pra tela principal
                    startActivity(new Intent(Login.this, SplashScrenn.class));
                    finish();
                } else {
                    DialogsUtils.mostrarToast(Login.this, "Credenciais inválidas", false);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                DialogsUtils.mostrarToast(Login.this, "Erro de conexão: " + t.getMessage(), false);
            }
        });
    }


}
