package com.example.scaneia.planilhas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.scaneia.Perfil;
import com.example.scaneia.R;
import com.example.scaneia.databinding.FragmentPlanilhaBinding;
import com.example.scaneia.model.NoticiaResponse;
import com.example.scaneia.utils.ImageExtractor;
import com.example.scaneia.utils.NotificationHelper;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanilhaFragment extends Fragment {

    private FragmentPlanilhaBinding binding;
    private List<NoticiaResponse> listaNoticias = new ArrayList<>();
    private Map<String, String> cacheImagens = new HashMap<>();
    private AdapterCarrossel adapter;

    private int tamanhoAnterior = 0;
    private static final String PREFS_NAME = "noticias";
    private static final String PREF_TAMANHO = "tamanhoAnterior";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlanilhaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        NotificationHelper.criarCanalNotificacao(getContext());

        // Recupera o tamanhoAnterior do SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        tamanhoAnterior = prefs.getInt(PREF_TAMANHO, 0);

        setupProfileButton(root);
        setupCarousel();
        fetchNoticiasFromFirebase();

        return root;
    }

    private void setupProfileButton(View root) {
        ImageView profile = root.findViewById(R.id.profile);
        profile.setOnClickListener(v -> startActivity(new Intent(getActivity(), Perfil.class)));
    }

    private void setupCarousel() {
        RecyclerView carouselRecyclerView = binding.carouselRecyclerView;
        carouselRecyclerView.setItemAnimator(null);

        CarouselLayoutManager layoutManager = new CarouselLayoutManager();
        carouselRecyclerView.setLayoutManager(layoutManager);

        adapter = new AdapterCarrossel(listaNoticias);
        carouselRecyclerView.setAdapter(adapter);

        CarouselSnapHelper snapHelper = new CarouselSnapHelper();
        snapHelper.attachToRecyclerView(carouselRecyclerView);
    }

    private void fetchNoticiasFromFirebase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("noticias");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int tamanhoAtual = (int) snapshot.getChildrenCount();

                listaNoticias.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    NoticiaResponse noticia = new NoticiaResponse();
                    String dataColeta = ds.child("data_coleta").getValue(String.class);
                    if (dataColeta != null && dataColeta.contains(" ")) {
                        dataColeta = dataColeta.split(" ")[0];
                    }
                    noticia.setData_coleta(dataColeta != null ? dataColeta : "Sem data");
                    String titulo = ds.child("\uFEFFtitulo").getValue(String.class);
                    System.out.println(titulo);
                    noticia.setTitulo(titulo != null ? titulo : "Sem título");
                    String linkDaNoticia = ds.child("link").getValue(String.class);
                    System.out.println(linkDaNoticia);
                    if (cacheImagens.containsKey(linkDaNoticia)) {
                        noticia.setLink(cacheImagens.get(linkDaNoticia));
                    }
                    else {
                        noticia.setLink(linkDaNoticia);
                        int pos = listaNoticias.size();
                        ImageExtractor.getImageFromUrl(linkDaNoticia, imageUrl -> {
                            String urlFinal = imageUrl != null ? imageUrl : linkDaNoticia;
                            cacheImagens.put(linkDaNoticia, urlFinal);
                            noticia.setLink(urlFinal);

                            if (adapter != null && pos < listaNoticias.size()) {
                                adapter.notifyItemChanged(pos);
                            }
                        });
                    }

                    noticia.setLinkOriginal(linkDaNoticia);
                    Toast.makeText(PlanilhaFragment.this.getContext(),
                            linkDaNoticia, Toast.LENGTH_SHORT).show();
                    listaNoticias.add(noticia);
                }

                adapter.notifyDataSetChanged();

                // Dispara notificação
                if (tamanhoAtual > tamanhoAnterior && listaNoticias.size() > 0) {
                    NoticiaResponse ultima = listaNoticias.get(listaNoticias.size() - 1);
                    NotificationHelper.mostrarNotificacao(
                            requireContext(),
                            "Nova notícia adicionada!",
                            ultima.getTitulo(),
                            ultima.getLinkOriginal()
                    );
                }

                // Atualiza tamanhoAnterior no SharedPreferences
                tamanhoAnterior = tamanhoAtual;
                SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit().putInt(PREF_TAMANHO, tamanhoAnterior).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Erro ao buscar notícias: " + error.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
