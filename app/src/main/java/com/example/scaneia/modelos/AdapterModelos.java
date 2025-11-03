package com.example.scaneia.modelos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scaneia.R;
import com.example.scaneia.model.FiltroInformacoesModelos;

import java.util.List;

public class AdapterModelos extends RecyclerView.Adapter<AdapterModelos.ViewHolder> {

    private final List<FiltroInformacoesModelos> modelos;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnModeloSelectedListener listener;

    public interface OnModeloSelectedListener {
        void onModeloSelected(FiltroInformacoesModelos modelo);
    }

    public AdapterModelos(List<FiltroInformacoesModelos> modelos) {
        this.modelos = modelos;
    }

    public void setOnModeloSelectedListener(OnModeloSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdapterModelos.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chip_modeloplanilha, parent, false);
        return new AdapterModelos.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterModelos.ViewHolder holder, int position) {
        FiltroInformacoesModelos modelo = modelos.get(position);

        holder.tvNomePlanilha.setText(modelo.getTitulo());
        holder.tvAtualizacao.setText("");
        holder.tvQtdRegistros.setText(String.valueOf(modelo.getNumeroRegistros()));

        holder.itemView.setSelected(selectedPosition == position);

        holder.itemView.setOnClickListener(v -> {
            int previous = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previous);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onModeloSelected(modelo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelos != null ? modelos.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomePlanilha, tvAtualizacao, tvQtdRegistros;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomePlanilha = itemView.findViewById(R.id.tv_nome_planilha);
            tvAtualizacao = itemView.findViewById(R.id.tv_atualizacao);
            tvQtdRegistros = itemView.findViewById(R.id.tv_qtd_registros);
            cardView = (CardView) itemView;
        }
    }
}
