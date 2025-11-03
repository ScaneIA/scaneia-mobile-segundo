package com.example.scaneia.solicitacoes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.scaneia.Perfil;
import com.example.scaneia.R;
import com.example.scaneia.databinding.FragmentSolicitacoesBinding;
import com.example.scaneia.model.NoticiaResponse;
import com.example.scaneia.utils.NotificationHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SolicitacoesFragment extends Fragment {

    private FragmentSolicitacoesBinding binding;
    private AdapterSolicitacoes adapter;
    private final List<NoticiaResponse> listaNoticias = new ArrayList<>();
    private int tamanhoAnterior = 0;

    private static final String PREFS_NOTICIAS = "noticias";
    private static final String PREF_TAMANHO = "tamanhoAnterior";

    private final SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat formatoDesejado = new SimpleDateFormat("dd/MM/yyyy");

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

        ImageView profile = root.findViewById(R.id.profile);
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Perfil.class);
            startActivity(intent);
        });

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("noticias");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                new Thread(() -> processFirebaseSnapshot(snapshot, prefs)).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Erro no Firebase: " + error.getMessage());
            }
        });

        return root;
    }

    private void processFirebaseSnapshot(@NonNull DataSnapshot snapshot, SharedPreferences prefs) {
        List<NoticiaResponse> tempList = new ArrayList<>();

        int tamanhoAtual = (int) snapshot.getChildrenCount();

        for (DataSnapshot ds : snapshot.getChildren()) {
            NoticiaResponse noticia = new NoticiaResponse();

            // Parse and format date
            String dataColeta = ds.child("data_coleta").getValue(String.class);
            Date parsedDate = null;

            if (dataColeta != null && dataColeta.contains(" ")) {
                dataColeta = dataColeta.split(" ")[0];
            }

            if (dataColeta != null) {
                try {
                    parsedDate = formatoOriginal.parse(dataColeta);
                    noticia.setData_coleta("Publicado em " + formatoDesejado.format(parsedDate));
                } catch (Exception e) {
                    noticia.setData_coleta("Data inválida");
                }
            } else {
                noticia.setData_coleta("Data indisponível");
            }

            noticia.setParsedDate(parsedDate);

            // Title
            String titulo = ds.child("\uFEFFtitulo").getValue(String.class);
            noticia.setTitulo(titulo != null ? titulo : "Sem título");

            // Link original only (image will be loaded lazily in adapter)
            noticia.setLinkOriginal(ds.child("link").getValue(String.class));

            tempList.add(noticia);
        }

        // Sort newest first
        Collections.sort(tempList, (o1, o2) -> {
            Date d1 = o1.getParsedDate();
            Date d2 = o2.getParsedDate();
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return 1;
            if (d2 == null) return -1;
            return d2.compareTo(d1);
        });

        new Handler(Looper.getMainLooper()).post(() -> {
            listaNoticias.clear();
            listaNoticias.addAll(tempList);
            adapter.notifyDataSetChanged();

            if (tamanhoAtual > tamanhoAnterior && !listaNoticias.isEmpty()) {
                NoticiaResponse ultima = listaNoticias.get(0);
                NotificationHelper.mostrarNotificacao(
                        requireContext(),
                        "Nova notícia adicionada!",
                        ultima.getTitulo(),
                        ultima.getLinkOriginal()
                );
            }

            tamanhoAnterior = tamanhoAtual;
            prefs.edit().putInt(PREF_TAMANHO, tamanhoAnterior).apply();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
