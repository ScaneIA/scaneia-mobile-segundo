package com.example.scaneia.solicitacoes;

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
import com.example.scaneia.databinding.FragmentSolicitacoesBinding;
import com.example.scaneia.model.NoticiaResponse;
import com.example.scaneia.utils.ImageCache;
import com.example.scaneia.utils.ImageExtractor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolicitacoesFragment extends Fragment {

    private FragmentSolicitacoesBinding binding;
    private AdapterSolicitacoes adapter;
    private List<NoticiaResponse> listaNoticias = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSolicitacoesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.rv;
        adapter = new AdapterSolicitacoes(listaNoticias);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Perfil
        ImageView profile = root.findViewById(R.id.profile);
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Perfil.class);
            startActivity(intent);
        });

        // Conectar com Firebase
        Query query = FirebaseDatabase.getInstance()
                .getReference("noticias")
                .limitToLast(10);
        //DatabaseReference database = FirebaseDatabase.getInstance().getReference("noticias");
        query.addValueEventListener(new ValueEventListener() {
            // Crie isso na classe do fragment
            private final Map<String, String> cacheImagens = new HashMap<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
                    try {
                        SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = formatoOriginal.parse(dataColeta);
                        SimpleDateFormat formatoDesejado = new SimpleDateFormat("dd/MM/yyyy");
                        noticia.setData_coleta("Publicado em " + formatoDesejado.format(date));
                    } catch (Exception e) {
                        noticia.setData_coleta(dataColeta);
                    }

                    // Título
                    String titulo = ds.child("\uFEFFtitulo").getValue(String.class);
                    noticia.setTitulo(titulo != null ? titulo : "Sem título");

                    // Link da notícia
                    String linkDaNoticia = ds.child("link").getValue(String.class);

                    // URL da imagem (cache persistente)
                    if (cache.contains(linkDaNoticia)) {
                        noticia.setLink(cache.get(linkDaNoticia));
                    }
                    else {
                        // usa link da notícia enquanto carrega a imagem
                        noticia.setLink(linkDaNoticia);

                        int pos = tempList.size(); // posição do item na lista temporária
                        ImageExtractor.getImageFromUrl(linkDaNoticia, imageUrl -> {
                            String urlFinal = imageUrl != null ? imageUrl : linkDaNoticia;

                            // salva no cache
                            cache.put(linkDaNoticia, urlFinal);

                            // atualiza o item do RecyclerView
                            noticia.setLink(urlFinal);
                            if (adapter != null && pos < listaNoticias.size()) {
                                adapter.notifyItemChanged(pos);
                            }
                        });
                    }

                    // Link da notícia
                    noticia.setLinkOriginal(linkDaNoticia);
                    tempList.add(noticia);
                }

                // Atualiza lista principal e RecyclerView
                listaNoticias.addAll(tempList);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("deu ruimm heinn: " + error.getMessage());
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
