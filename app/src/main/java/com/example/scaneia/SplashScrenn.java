package com.example.scaneia;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                abrirTela();
            }
        }, 1500);

    }
    void abrirTela() {
        String accessToken = ApiClient.getAccessToken();
        Log.i("AccessToken", accessToken);

        if (accessToken != null) {
            UserInfo userinfo = JwtUtils.decodeUserAndRole(accessToken);

            if (userinfo != null ) {
                String username = userinfo.getUsername();
                int role = userinfo.getIdTipoUsuario();

                Log.d("LOGIN", "Usuário: " + username + " | Tipo: " + role);

                switch (role) {
                    case 4: {
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
                    case 1: {
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
                    case 2: {
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