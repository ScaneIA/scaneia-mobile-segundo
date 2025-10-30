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


public class PlanilhaFragment extends Fragment {

    private FragmentPlanilhaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlanilhaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.rv;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        // Cria a interface da API
//        ScaneiaApiMongo scaneiaApi = RetrofitClient.getClientMongo().create(ScaneiaApiMongo.class);
//
//        // Faz a requisição
//        scaneiaApi.getFiltroInformacoesModelos().enqueue(new Callback<List<FiltroInformacoesModelos>>() {
//            @Override
//            public void onResponse(Call<List<FiltroInformacoesModelos>> call, Response<List<FiltroInformacoesModelos>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<FiltroInformacoesModelos> modelos = response.body();
//
//                    // Preenche o adapter com os dados reais
//                    AdapterPlanilha adapter = new AdapterPlanilha(modelos);
//                    recyclerView.setAdapter(adapter);
//                }
//            }
//            @Override
//            public void onFailure(Call<List<FiltroInformacoesModelos>> call, Throwable t) {
//                System.out.println("eitaa, deu ruimm");
//                t.printStackTrace();
//            }
//        });


        ImageView profile = root.findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Perfil.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}