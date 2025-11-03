package com.example.scaneia.api;

import com.example.scaneia.model.FiltroInformacoesModelos;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ScaneiaApiMongo {
    @GET("escaneamentos/filtro")
    Call<List<FiltroInformacoesModelos>> getFiltroInformacoesModelos();

    @POST("escaneamentos")
    Call<Void> postEscaneamento(@Body Map<String, Object> body);
}
