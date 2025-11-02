package com.example.scaneia.planilhas;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.scaneia.NoticiaWebViewActivity;
import com.example.scaneia.R;
import com.example.scaneia.model.NoticiaResponse;

import java.util.List;

public class AdapterCarrossel extends RecyclerView.Adapter<AdapterCarrossel.ViewHolder> {
    private final List<NoticiaResponse> lista;

    public AdapterCarrossel(List<NoticiaResponse> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_carrossel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoticiaResponse noticia = lista.get(position);

        Glide.with(holder.itemView.getContext())
                .load(noticia.getLink())
                .placeholder(R.drawable.dialog_erro)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            String link = noticia.getLink();

            if (link == null || link.isEmpty()) {
                link = noticia.getLink(); // tenta usar o link da imagem
            }

            if (link == null || link.isEmpty()) {
                Toast.makeText(holder.itemView.getContext(),
                        "Link da notícia indisponível 😕", Toast.LENGTH_SHORT).show();
                return;
            }

            System.out.println("🔗 Abrindo link: " + link);

            Intent intent = new Intent(holder.itemView.getContext(), NoticiaWebViewActivity.class);
            intent.putExtra(NoticiaWebViewActivity.EXTRA_LINK, link);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageCarrossel);
        }
    }
}
