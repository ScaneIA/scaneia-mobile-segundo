package com.example.scaneia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.scaneia.api.ApiProxy;
import com.example.scaneia.model.CadastroRequest;
import com.example.scaneia.utils.DialogsUtils;
import java.io.IOException;

public class PrimeiroAcesso extends AppCompatActivity {

    private ApiProxy apiProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_primeiro_acesso);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiProxy = new ApiProxy();

        EditText emailView = findViewById(R.id.email);
        EditText senhaView = findViewById(R.id.senha);
        EditText senhaConfirmacaoView = findViewById(R.id.senhaConfirmacao);
        Button btnCadastrar = findViewById(R.id.cadastrar);

        btnCadastrar.setOnClickListener(v -> {
            String email = emailView.getText().toString().trim();
            String senha = senhaView.getText().toString().trim();
            String senhaConfirmacao = senhaConfirmacaoView.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty() || senhaConfirmacao.isEmpty()) {
                DialogsUtils.mostrarToast(this, "Preencha todos os campos", false);
                return;
            }

            if (!senha.equals(senhaConfirmacao)) {
                DialogsUtils.mostrarToast(this, "As senhas não coincidem", false);
                return;
            }

            CadastroRequest cadastroRequest = new CadastroRequest(senha, email);

            new Thread(() -> {
                try {
                    int statusCode = apiProxy.cadastrarUsuario(cadastroRequest);

                    runOnUiThread(() -> {
                        switch (statusCode) {
                            case 201:
                                DialogsUtils.mostrarToast(this, "Senha cadastrada com sucesso!", true);
                                startActivity(new Intent(this, Login.class));
                                finish();
                                break;
                            case 404:
                                DialogsUtils.mostrarToast(this, "Usuário não encontrado", false);
                                break;
                            case 409:
                                DialogsUtils.mostrarToast(this, "Senha já cadastrada", false);
                                break;
                            default:
                                DialogsUtils.mostrarToast(this, "Erro inesperado (" + statusCode + ")", false);
                                break;
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            DialogsUtils.mostrarToast(this, "Falha de conexão com o servidor", false)
                    );
                }
            }).start();
        });
    }
}
