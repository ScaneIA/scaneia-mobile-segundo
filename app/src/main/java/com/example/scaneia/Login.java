package com.example.scaneia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.scaneia.api.ApiClient;
import com.example.scaneia.api.ApiProxy;
import com.example.scaneia.model.LoginRequest;
import com.example.scaneia.model.LoginResponse;
import com.example.scaneia.model.UserInfo;
import com.example.scaneia.utils.DialogsUtils;
import com.example.scaneia.utils.JwtUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

public class Login extends AppCompatActivity {

    private TextInputEditText editEmail, editSenha;
    private Button btnEntrar;
    private ApiProxy apiProxy;

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

        apiProxy = new ApiProxy();

        // Check saved token using TokenManager
        String refreshToken = ApiClient.getRefreshToken();
        if (refreshToken != null) {
            UserInfo info = JwtUtils.decodeUserAndRole(refreshToken);
            if (info != null) {
                startActivity(new Intent(Login.this, SplashScrenn.class));
                finish();
                return;
            } else {
                ApiClient.setTokens(null, null); // remove invalid tokens
            }
        }

        btnEntrar.setOnClickListener(v -> fazerLogin());

        TextView btnCadastrar = findViewById(R.id.cadastro);
        btnCadastrar.setOnClickListener(v ->
                startActivity(new Intent(Login.this, PrimeiroAcesso.class))
        );
    }

    private void fazerLogin() {
        String username = editEmail.getText().toString().trim();
        String password = editSenha.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
           // DialogsUtils.mostrarToast(this, "");
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                LoginRequest request = new LoginRequest(username, password);
                LoginResponse response = apiProxy.login(request);

                if (response != null) {
                    ApiClient.setTokens(response.getAccessToken(), response.getRefreshToken());

                    runOnUiThread(() -> {
                        Toast.makeText(Login.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, SplashScrenn.class));
                        finish();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(Login.this, "Credenciais inválidas!", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(Login.this, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
