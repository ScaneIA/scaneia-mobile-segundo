package com.example.scaneia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterPlanilha extends RecyclerView.Adapter<AdapterPlanilha.ViewHolder>{

    private int quantidade;

    public AdapterPlanilha(int quantidade){
        this.quantidade = quantidade;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_planilha, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return quantidade;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textPlaca, textEntrada, textSaida;
        ConstraintLayout fundo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}

