package com.example.scaneia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.scaneia.api.RetrofitClient;
import com.example.scaneia.api.ScaneiaApiSQL;
import com.example.scaneia.model.LogoutRequest;
import com.example.scaneia.model.UsuarioPerfilResponse;
import com.example.scaneia.planilhas.PlanilhaFragment;
import com.example.scaneia.utils.DialogsUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Perfil extends AppCompatActivity {

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

        ScaneiaApiSQL scaneiaApi = RetrofitClient.getClientSQL().create(ScaneiaApiSQL.class);
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String refreshToken = prefs.getString("refresh_token", null);
        String accessToken = prefs.getString("access_token", null);

        if (accessToken != null) {
            Call<UsuarioPerfilResponse> call = scaneiaApi.getUsuarioPerfil("Bearer " + accessToken);
            call.enqueue(new Callback<UsuarioPerfilResponse>() {
                @Override
                public void onResponse(Call<UsuarioPerfilResponse> call, Response<UsuarioPerfilResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String tnome = response.body().getNome();
                        String temail = response.body().getEmail();
                        Date tdataContratacao = response.body().getDataCriacao();
                        String tcpf = response.body().getCpf();
                        ((TextView) findViewById(R.id.nomeCompleto)).setText(tnome);
                        ((TextView) findViewById(R.id.email)).setText(temail);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        ((TextView) findViewById(R.id.dataContratacao)).setText(sdf.format(tdataContratacao));
                        ((TextView) findViewById(R.id.cpf)).setText(tcpf);
                        ((TextView) findViewById(R.id.mensagem)).setText("Olá, " + tnome);
                    }
                    else {
                        DialogsUtils.mostrarToast(Perfil.this, "Erro no carregamento dos dados", false);
                    }
                }

                @Override
                public void onFailure(Call<UsuarioPerfilResponse> call, Throwable t) {
                    DialogsUtils.mostrarToast(Perfil.this, "Erro de conexão: " + t.getMessage(), false);
                }
            });

        }
        else {
            DialogsUtils.mostrarToast(Perfil.this, "Reestabelecendo conexão...", false);
        }

        ImageView voltar = findViewById(R.id.voltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(Perfil.this, PlanilhaFragment.class);
                startActivity(home);
            }
        });

        Button sair = findViewById(R.id.sair);
        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (refreshToken != null) {
                    LogoutRequest request = new LogoutRequest(refreshToken);
                    Call<Void> call = scaneiaApi.logout(request);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                DialogsUtils.mostrarToast(Perfil.this, "Logout realizado com sucesso", true);
                                //Limpa tudo do SharedPreferences
                                prefs.edit().clear().apply();

                                //Redireciona pra tela de login
                                Intent loginIntent = new Intent(Perfil.this, Login.class);
                                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntent);
                            }
                            else {
                                DialogsUtils.mostrarToast(Perfil.this, "Falha ao sair do aplicativo", false);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            DialogsUtils.mostrarToast(Perfil.this, "Erro de conexão: " + t.getMessage(), false);
                        }
                    });
                }
                else {
                    // Se não tiver refresh token salvo, só volta pro login
                    DialogsUtils.mostrarToast(Perfil.this, "Reestabelecendo conexão...", false);
                    Intent loginIntent = new Intent(Perfil.this, Login.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        });

    }
}
