package com.example.scaneia.planilhas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlanilhaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.rv;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        apiProxy = new ApiProxy();

        // Carrega os dados usando ApiProxy
        carregarModelos(recyclerView);

        ImageView profile = root.findViewById(R.id.profile);
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Perfil.class);
            startActivity(intent);
        });
        return root;
    }

    private void carregarModelos(RecyclerView recyclerView) {
        new Thread(() -> {
            try {
                List<FiltroInformacoesModelos> modelos = apiProxy.getFiltroInformacoesModelos();

                if (modelos != null) {
                    getActivity().runOnUiThread(() -> {
                        AdapterPlanilha adapter = new AdapterPlanilha(modelos);
                        recyclerView.setAdapter(adapter);
                    });
                } else {
                    getActivity().runOnUiThread(() ->
                            System.out.println("Erro: lista de modelos vazia")
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() ->
                        System.out.println("Erro ao carregar modelos: " + e.getMessage())
                );
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
