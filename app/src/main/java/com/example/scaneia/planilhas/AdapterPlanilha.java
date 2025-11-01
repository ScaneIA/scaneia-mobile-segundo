package com.example.scaneia.planilhas;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.scaneia.R;
import com.example.scaneia.model.FiltroInformacoesModelos;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.List;

public class AdapterPlanilha extends RecyclerView.Adapter<AdapterPlanilha.ViewHolder>{

    private List<FiltroInformacoesModelos> modelos;

    public AdapterPlanilha(List<FiltroInformacoesModelos> modelos){
        this.modelos = modelos;
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
        FiltroInformacoesModelos modelo = modelos.get(position);

        holder.textTitulo.setText(modelo.getTitulo());
        holder.textSetor.setText(modelo.getEstrutura());
        holder.textQntRegistros.setText(String.valueOf(modelo.getNumeroRegistros()));

        if (modelo.getColunas() != null) {
            holder.colunas.removeAllViews(); // limpa chips anteriores

            for (String coluna : modelo.getColunas()) {
                // Cria o chip com o estilo do XML
                Chip chip = new Chip(
                        new ContextThemeWrapper(holder.colunas.getContext(), R.style.DefaultChipStyle),
                        null,
                        0
                );
                chip.setText(coluna);
                chip.setClickable(false);
                holder.colunas.addView(chip);
            }
        }
    }

    @Override
    public int getItemCount() {
        return modelos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitulo, textSetor, textQntRegistros;
        ChipGroup colunas;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.titulo);
            textSetor = itemView.findViewById(R.id.setor);
            textQntRegistros = itemView.findViewById(R.id.qntRegistros);
            colunas = itemView.findViewById(R.id.colunas);
        }
    }


}

