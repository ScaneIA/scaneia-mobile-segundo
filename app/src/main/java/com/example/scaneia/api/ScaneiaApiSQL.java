package com.example.scaneia.api;

import com.example.scaneia.model.LoginRequest;
import com.example.scaneia.model.LoginResponse;
import com.example.scaneia.model.LogoutRequest;
import com.example.scaneia.model.RefreshTokenRequestDTO;
import com.example.scaneia.model.UsuarioPerfilResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
public interface ScaneiaApiSQL {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/logout")
    Call<Void> logout(@Body LogoutRequest request);

    @GET("usuarios/filtro")
    Call<UsuarioPerfilResponse> getUsuarioPerfil(@Header("Authorization") String authHeader);

    @POST("auth/role")
    Call<String> recuperarRoleUsuario(@Body RefreshTokenRequestDTO refreshTokenRequestDTO);

}
