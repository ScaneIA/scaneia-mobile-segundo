package com.example.scaneia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.scaneia.api.ApiClient;
import com.example.scaneia.api.ApiProxy;
import com.example.scaneia.model.LogoutRequest;
import com.example.scaneia.model.UsuarioPerfilResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Perfil extends AppCompatActivity {

    private ApiProxy apiProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiProxy = new ApiProxy();

        carregarPerfil();

        ImageView voltar = findViewById(R.id.voltar);
        voltar.setOnClickListener(v -> startActivity(new Intent(Perfil.this, SplashScrenn.class)));

        Button sair = findViewById(R.id.sair);
        sair.setOnClickListener(v -> fazerLogout());
    }

    private void carregarPerfil() {
        new Thread(() -> {
            try {
                UsuarioPerfilResponse perfil = apiProxy.getUsuarioPerfil();

                if (perfil != null) {
                    String nome = perfil.getNome();
                    String email = perfil.getEmail();
                    Date dataContratacao = perfil.getDataCriacao();
                    String cpf = perfil.getCpf();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                    runOnUiThread(() -> {
                        ((TextView) findViewById(R.id.nomeCompleto)).setText(nome);
                        ((TextView) findViewById(R.id.email)).setText(email);
                        ((TextView) findViewById(R.id.dataContratacao)).setText(sdf.format(dataContratacao));
                        ((TextView) findViewById(R.id.cpf)).setText(cpf);
                        ((TextView) findViewById(R.id.mensagem)).setText("Olá, " + nome);
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(Perfil.this, "Erro no carregamento dos dados", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(Perfil.this, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void fazerLogout() {
        String refreshToken = ApiClient.getRefreshToken();

        if (refreshToken == null) {
            irParaLogin();
            return;
        }

        new Thread(() -> {
            try {
                apiProxy.logout(new LogoutRequest(refreshToken));
                ApiClient.setTokens(null, null); // clear tokens

                runOnUiThread(() -> {
                    Toast.makeText(Perfil.this, "Logout realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    irParaLogin();
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(Perfil.this, "Erro ao sair: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void irParaLogin() {
        Intent loginIntent = new Intent(Perfil.this, Login.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }
}
