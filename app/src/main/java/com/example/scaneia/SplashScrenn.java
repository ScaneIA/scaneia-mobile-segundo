package com.example.scaneia;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.scaneia.api.ApiClient;
import com.example.scaneia.databinding.ActivityAdminBinding;
import com.example.scaneia.databinding.ActivityDiretorBinding;
import com.example.scaneia.databinding.ActivityOperarioBinding;
import com.example.scaneia.model.UserInfo;
import com.example.scaneia.utils.JwtUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SplashScrenn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screnn);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView quadradinhos = findViewById(R.id.quadradinhos);

        //1
//        ObjectAnimator anim = ObjectAnimator.ofFloat(quadradinhos, "translationY", 0f, -200f);
//        anim.setDuration(3000);
//        anim.setRepeatCount(ValueAnimator.INFINITE);
//        anim.start();

        //2
        // Cria duas animações: uma de subida e outra de aparecimento
//        ObjectAnimator subir = ObjectAnimator.ofFloat(quadradinhos, "translationY", 300f, 0f);
//        ObjectAnimator aparecer = ObjectAnimator.ofFloat(quadradinhos, "alpha", 0f, 1f);
//
//        // Controla a duração e o tempo de início das animações
//        subir.setDuration(2000);
//        aparecer.setDuration(2000);
//
//        // Faz as duas animações rodarem ao mesmo tempo
//        AnimatorSet animSet = new AnimatorSet();
//        animSet.playTogether(subir, aparecer);
//        animSet.start();

        //3
        float alturaTela = getResources().getDisplayMetrics().heightPixels;

        ObjectAnimator subir = ObjectAnimator.ofFloat(quadradinhos, "translationY", 0f, -alturaTela);
        ObjectAnimator aparecer = ObjectAnimator.ofFloat(quadradinhos, "alpha", 0f, 1f);

        subir.setDuration(3000);
        aparecer.setDuration(1000);

        subir.setInterpolator(new DecelerateInterpolator());

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(subir, aparecer);
        animSet.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                abrirTela();
            }
        }, 2300);

    }

    void abrirTela() {
        String accessToken = ApiClient.getAccessToken();

        if (accessToken != null) {
            UserInfo info = JwtUtils.decodeUserAndRole(accessToken);

            if (info != null && info.getUsuario_tipo() != null) {
                String username = info.getUsername();
                String role = info.getUsuario_tipo();

                Log.d("LOGIN", "Usuário: " + username + " | Tipo: " + role);

                switch (role) {
                    case "COLABORADOR": {
                        ActivityOperarioBinding binding = ActivityOperarioBinding.inflate(getLayoutInflater());
                        setContentView(binding.getRoot());
                        BottomNavigationView navView = findViewById(R.id.nav_view_operario);
                        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                                R.id.planilhasFragment, R.id.solicitacoesFragment)
                                .build();
                        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_operario);
                        NavigationUI.setupWithNavController(binding.navViewOperario, navController);

                        binding.fabNewScreen.setOnClickListener(v -> {
                            binding.navViewOperario.setSelectedItemId(R.id.modelosFragment);
                        });
                        break;
                    }
                    case "DIRETOR": {
                        ActivityDiretorBinding binding = ActivityDiretorBinding.inflate(getLayoutInflater());
                        setContentView(binding.getRoot());
                        BottomNavigationView navView = findViewById(R.id.nav_view_diretor);
                        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                                R.id.planilhasFragment, R.id.analisesFragment, R.id.analisesFragment)
                                .build();
                        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_diretor);
                        NavigationUI.setupWithNavController(binding.navViewDiretor, navController);
                        break;
                    }
                    case "ADMIN": {
                        ActivityAdminBinding binding = ActivityAdminBinding.inflate(getLayoutInflater());
                        setContentView(binding.getRoot());
                        BottomNavigationView navView = findViewById(R.id.nav_view_admin);
                        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                                R.id.planilhasFragment, R.id.solicitacoesFragment, R.id.analisesFragment, R.id.funcionariosFragment)
                                .build();
                        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_admin);
                        NavigationUI.setupWithNavController(binding.navViewAdmin, navController);
                        break;
                    }
                    default:
                        Log.w("LOGIN", "Unknown user role: " + role);
                }
            } else {
                startActivity(new Intent(this, Login.class));
                finish();
                Log.w("LOGIN", "Invalid token or missing user info");
            }
        } else {
            startActivity(new Intent(this, Login.class));
            finish();
            Log.d("LOGIN", "No access token found");
        }
    }




}