package com.example.scaneia.planilhas;

import android.app.Activity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.scaneia.R;
import com.example.scaneia.api.ApiProxy;
import com.example.scaneia.model.EstruturaResponse;
import com.example.scaneia.model.FiltroInformacoesModelos;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.io.IOException;
import java.util.List;

public class AdapterPlanilha extends RecyclerView.Adapter<AdapterPlanilha.ViewHolder>{

    private List<FiltroInformacoesModelos> modelos;

    private ApiProxy apiProxy;

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

        apiProxy = new ApiProxy();

        holder.textTitulo.setText(modelo.getTitulo());
//        holder.textSetor.setText(modelo.getEstrutura());
        holder.textQntRegistros.setText(String.valueOf(modelo.getNumeroRegistros()));
        System.out.println(modelo.getEstrutura());
        System.out.println(modelo.getTitulo());
        System.out.println(modelo.getNumeroRegistros());

        //carregarModelos(modelo.getEstrutura(), holder.textSetor);

        if (modelo.getColunas() != null) {
            holder.colunas.removeAllViews();

            for (String coluna : modelo.getColunas()) {
                Chip chip = (Chip) LayoutInflater.from(holder.colunas.getContext())
                        .inflate(R.layout.item_chip, holder.colunas, false);
                chip.setText(coluna);
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

    private void carregarModelos(int id, TextView textSetor) {
        new Thread(() -> {
            try {
                EstruturaResponse estrutura = apiProxy.getEstruturaById(id);

                if (estrutura != null) {
                    ((Activity) textSetor.getContext()).runOnUiThread(() ->
                            textSetor.setText(estrutura.getDescricao())
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
                ((Activity) textSetor.getContext()).runOnUiThread(() ->
                        textSetor.setText("Erro ao carregar setor")
                );
            }
        }).start();
    }




}

