package com.example.scaneia.funcionarios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.scaneia.R;
import com.example.scaneia.model.UsuarioHierarquia;
import java.util.List;

public class AdapterFuncionarios extends RecyclerView.Adapter<AdapterFuncionarios.ViewHolder> {

    private List<UsuarioHierarquia> listaFuncionarios;

    public AdapterFuncionarios(List<UsuarioHierarquia> listaFuncionarios) {
        this.listaFuncionarios = listaFuncionarios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_funcionarios, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UsuarioHierarquia funcionario = listaFuncionarios.get(position);
        holder.textNomeUsuario.setText(funcionario.getNomeUsuario());
        holder.textDescricaoUsuarioTipo.setText(funcionario.getDescricaoUsuarioTipo());
    }

    @Override
    public int getItemCount() {
        return listaFuncionarios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNomeUsuario, textDescricaoUsuarioTipo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNomeUsuario = itemView.findViewById(R.id.textNomeUsuario);
            textDescricaoUsuarioTipo = itemView.findViewById(R.id.textDescricaoUsuarioTipo);
        }
    }
}
