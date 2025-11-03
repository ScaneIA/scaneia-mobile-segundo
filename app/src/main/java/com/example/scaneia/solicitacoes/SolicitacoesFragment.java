package com.example.scaneia.solicitacoes;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.scaneia.databinding.FragmentSolicitacoesBinding;
import com.example.scaneia.model.NoticiaResponse;
import com.example.scaneia.utils.ImageCache;
import com.example.scaneia.utils.ImageExtractor;
import com.example.scaneia.utils.NotificationHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SolicitacoesFragment extends Fragment {

    private FragmentSolicitacoesBinding binding;
    private AdapterSolicitacoes adapter;
    private List<NoticiaResponse> listaNoticias = new ArrayList<>();

    private int tamanhoAnterior = 0;

    // Renomeado para evitar conflito com o TokenManager
    private static final String PREFS_NOTICIAS = "noticias";
    private static final String PREF_TAMANHO = "tamanhoAnterior";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSolicitacoesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.rv;
        adapter = new AdapterSolicitacoes(listaNoticias);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NOTICIAS, Context.MODE_PRIVATE);
        tamanhoAnterior = prefs.getInt(PREF_TAMANHO, 0);

        // Botão do perfil
        ImageView profile = root.findViewById(R.id.profile);
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Perfil.class);
            startActivity(intent);
        });

        // Conexão com Firebase
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("noticias");
        database.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int tamanhoAtual = (int) snapshot.getChildrenCount();
                listaNoticias.clear();
                List<NoticiaResponse> tempList = new ArrayList<>();
                ImageCache cache = ImageCache.getInstance();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    NoticiaResponse noticia = new NoticiaResponse();

                    // Formata a data
                    String dataColeta = ds.child("data_coleta").getValue(String.class);
                    if (dataColeta != null && dataColeta.contains(" ")) {
                        dataColeta = dataColeta.split(" ")[0];
                    }

                    if (dataColeta != null) {
                        try {
                            SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = formatoOriginal.parse(dataColeta);
                            SimpleDateFormat formatoDesejado = new SimpleDateFormat("dd/MM/yyyy");
                            noticia.setData_coleta("Publicado em " + formatoDesejado.format(date));
                        } catch (Exception e) {
                            noticia.setData_coleta(dataColeta);
                        }
                    } else {
                        noticia.setData_coleta("Data indisponível");
                    }

                    // Título
                    String titulo = ds.child("\uFEFFtitulo").getValue(String.class);
                    noticia.setTitulo(titulo != null ? titulo : "Sem título");

                    // Link da notícia
                    String linkDaNoticia = ds.child("link").getValue(String.class);

                    // URL da imagem (cache persistente)
                    if (cache.contains(linkDaNoticia)) {
                        noticia.setLink(cache.get(linkDaNoticia));
                    } else {
                        // Usa link da notícia enquanto carrega a imagem
                        noticia.setLink(linkDaNoticia);

                        ImageExtractor.getImageFromUrl(linkDaNoticia, imageUrl -> {
                            String urlFinal = imageUrl != null ? imageUrl : linkDaNoticia;
                            cache.put(linkDaNoticia, urlFinal);

                            noticia.setLink(urlFinal);
                            int index = listaNoticias.indexOf(noticia);
                            if (adapter != null && index >= 0) {
                                adapter.notifyItemChanged(index);
                            }
                        });
                    }

                    noticia.setLinkOriginal(linkDaNoticia);
                    tempList.add(noticia);
                }

                // Atualiza lista principal e RecyclerView
                listaNoticias.addAll(tempList);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }

                // Dispara notificação se houver nova notícia
                if (tamanhoAtual > tamanhoAnterior && !listaNoticias.isEmpty()) {
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
                prefs.edit().putInt(PREF_TAMANHO, tamanhoAnterior).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Erro no Firebase: " + error.getMessage());
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
