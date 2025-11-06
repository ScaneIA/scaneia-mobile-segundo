package com.example.scaneia.api;

import com.example.scaneia.model.CadastroRequest;
import com.example.scaneia.model.EstruturaResponse;
import com.example.scaneia.model.LoginRequest;
import com.example.scaneia.model.LoginResponse;
import com.example.scaneia.model.LogoutRequest;
import com.example.scaneia.model.RefreshTokenRequestDTO;
import com.example.scaneia.model.UsuarioHierarquia;
import com.example.scaneia.model.UsuarioPerfilResponse;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
public interface ScaneiaApiSQL {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/logout")
    Call<Void> logout(@Body LogoutRequest request);

    @POST("usuarios/perfil")
    Call<UsuarioPerfilResponse> getUsuarioPerfil();

    @POST("auth/role")
    Call<String> recuperarRoleUsuario(@Body RefreshTokenRequestDTO refreshTokenRequestDTO);

    @POST("auth/refresh")
    Call<LoginResponse> refresh(@Body RefreshTokenRequestDTO refreshTokenRequestDTO);

    @POST("/vision/analyze")
    Call<Map<String, Object>> analyzeImageByUrl(@Body Map<String, String> body);

    @GET("estrutura/{id}")
    Call<EstruturaResponse> getEstruturaById(@Path("id") int id);

    @GET("usuarios/hierarquia/{id}")
    Call<List<UsuarioHierarquia>> getUsuarioHierarquia(@Path("id") int id);

    @POST("auth/cadastro")
    Call<Void> cadastrarUsuario(@Body CadastroRequest request);
}
