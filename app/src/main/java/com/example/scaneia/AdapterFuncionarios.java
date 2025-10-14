package com.example.scaneia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterFuncionarios extends RecyclerView.Adapter<AdapterFuncionarios.ViewHolder>{
    private int quantidade;

    public AdapterFuncionarios(int quantidade){
        this.quantidade = quantidade;
    }

    @NonNull
    @Override
    public AdapterFuncionarios.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_funcionarios, parent, false);
        return new AdapterFuncionarios.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return quantidade;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textPlaca, textEntrada, textSaida;
        ConstraintLayout fundo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
