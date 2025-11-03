package com.example.scaneia.solicitacoes;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.scaneia.NoticiaWebViewActivity;
import com.example.scaneia.R;
import com.example.scaneia.model.NoticiaResponse;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AdapterSolicitacoes extends RecyclerView.Adapter<AdapterSolicitacoes.ViewHolder>{

    private List<NoticiaResponse> lista;

    public AdapterSolicitacoes(List<NoticiaResponse> lista){
        this.lista = lista;
    }

    @NonNull
    @Override
    public AdapterSolicitacoes.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_test, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSolicitacoes.ViewHolder holder, int position) {
        NoticiaResponse noticia = lista.get(position);

        holder.textTitulo.setText(noticia.getTitulo());
        holder.textDataColeta.setText(noticia.getData_coleta());

        Glide.with(holder.itemView.getContext())
                .load(noticia.getLink())
                .placeholder(R.drawable.dialog_erro)
                .into(holder.textLink);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), NoticiaWebViewActivity.class);
            System.out.println(noticia.getLinkOriginal());
            intent.putExtra(NoticiaWebViewActivity.EXTRA_LINK, noticia.getLinkOriginal());
            holder.itemView.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitulo, textDataColeta;

        ShapeableImageView textLink;
        LinearLayout fundo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.titulo);
            textLink = itemView.findViewById(R.id.link);
            textDataColeta = itemView.findViewById(R.id.dataColeta);
            fundo = itemView.findViewById(R.id.fundo);
        }
    }
}