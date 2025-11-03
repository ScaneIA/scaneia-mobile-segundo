package com.example.scaneia.planilhas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.scaneia.Perfil;
import com.example.scaneia.R;
import com.example.scaneia.databinding.FragmentPlanilhaBinding;
import com.example.scaneia.model.FiltroInformacoesModelos;
import com.example.scaneia.api.ApiProxy;

import java.io.IOException;
import java.util.List;

public class PlanilhaFragment extends Fragment {
    private FragmentPlanilhaBinding binding;
    private ApiProxy apiProxy;
    private static final int MIN_LOADING_TIME_MS = 1800;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlanilhaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.rv;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        LottieAnimationView loadingView = binding.lottieAnimationView;

        apiProxy = new ApiProxy();

        loadingView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        root.post(() -> carregarModelos(recyclerView, loadingView));

        ImageView profile = root.findViewById(R.id.profile);
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Perfil.class);
            startActivity(intent);
        });
        return root;
    }

    private void carregarModelos(RecyclerView recyclerView, LottieAnimationView loadingView) {
        long startTime = System.currentTimeMillis();

        new Thread(() -> {
            try {
                List<FiltroInformacoesModelos> modelos = apiProxy.getFiltroInformacoesModelos();

                long elapsed = System.currentTimeMillis() - startTime;
                long remainingDelay = Math.max(0, MIN_LOADING_TIME_MS - elapsed);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    loadingView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    if (modelos != null && !modelos.isEmpty()) {
                        AdapterPlanilha adapter = new AdapterPlanilha(modelos);
                        recyclerView.setAdapter(adapter);
                    } else {
                        System.out.println("Erro: lista de modelos vazia");
                    }
                }, remainingDelay);

            } catch (IOException e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    loadingView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    System.out.println("Erro ao carregar modelos: " + e.getMessage());
                });
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
