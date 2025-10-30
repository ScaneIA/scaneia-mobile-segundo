package com.example.scaneia.api;

import com.example.scaneia.model.FiltroInformacoesModelos;

import java.util.List;

import okhttp3.Cache;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ScaneiaApiMongo {
    @GET("escaneamentos/filtro")
    Call<List<FiltroInformacoesModelos>> getFiltroInformacoesModelos();
}
