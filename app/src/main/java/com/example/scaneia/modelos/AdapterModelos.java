package com.example.scaneia.modelos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scaneia.R;

public class AdapterModelos extends RecyclerView.Adapter<AdapterModelos.ViewHolder>{
    private int quantidade;

    public AdapterModelos(int quantidade){
        this.quantidade = quantidade;
    }

    @NonNull
    @Override
    public AdapterModelos.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chip_modeloplanilha, parent, false);
        return new AdapterModelos.ViewHolder(view);
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
