package com.example.scaneia.funcionarios;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.scaneia.Login;
import com.example.scaneia.Perfil;
import com.example.scaneia.R;
import com.example.scaneia.api.ApiClient;
import com.example.scaneia.api.ApiProxy;
import com.example.scaneia.databinding.FragmentFuncionariosBinding;
import com.example.scaneia.model.UserInfo;
import com.example.scaneia.model.UsuarioHierarquia;
import com.example.scaneia.utils.DialogsUtils;
import com.example.scaneia.utils.JwtUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;

public class FuncionariosFragment extends Fragment {

    private FragmentFuncionariosBinding binding;

    private ApiProxy apiProxy;


    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFuncionariosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView imgSemFuncionarios = root.findViewById(R.id.imgSemFuncionarios);
        imgSemFuncionarios.setVisibility(View.GONE);

        TextView mensagemColaborador = root.findViewById(R.id.mensagemColaborador);

        apiProxy = new ApiProxy();

        String accessToken = ApiClient.getAccessToken();
        System.out.println(accessToken);
        if (accessToken != null) {
            UserInfo userinfo = JwtUtils.decodeUserAndRole(accessToken);
            mensagemColaborador.setText("Olá, " + extrairNomeDeEmail(userinfo.getUsername()));

            RecyclerView recyclerView = binding.rv;
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            apiProxy = new ApiProxy();

            // Carrega os dados usando ApiProxy
            carregarFuncionarios(recyclerView, userinfo.getId(), imgSemFuncionarios);

        }
        else {
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
        }

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
    private void carregarFuncionarios(RecyclerView recyclerView, int id, ImageView imgSemFuncionarios) {
        System.out.println(id);
        new Thread(() -> {
            try {
                List<UsuarioHierarquia> funcionarios = apiProxy.getUsuarioHierarquia(id);
                if (!funcionarios.isEmpty()) {
                    getActivity().runOnUiThread(() -> {
                        AdapterFuncionarios adapter = new AdapterFuncionarios(funcionarios);
                        recyclerView.setAdapter(adapter);
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        DialogsUtils.mostrarToast(getContext(), "Lista de funcionários vazia", false);
                        imgSemFuncionarios.setVisibility(View.VISIBLE);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() ->
                        DialogsUtils.mostrarToast(getContext(), "Erro ao carregar lista de funcionários: " + e.getMessage(), false)
                );
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static String extrairNomeDeEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "";
        }

        // Pega apenas a parte antes do "@"
        String parteAntesDoArroba = email.split("@")[0];

        // Divide por ponto (.)
        String[] partes = parteAntesDoArroba.split("\\.");

        // Monta o nome com a primeira letra maiúscula
        StringBuilder nomeFormatado = new StringBuilder();

        for (String parte : partes) {
            if (!parte.isEmpty()) {
                // Primeira letra maiúscula + resto minúsculo
                nomeFormatado.append(Character.toUpperCase(parte.charAt(0)))
                        .append(parte.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        // Remove espaço final e retorna
        return nomeFormatado.toString().trim();
    }
}