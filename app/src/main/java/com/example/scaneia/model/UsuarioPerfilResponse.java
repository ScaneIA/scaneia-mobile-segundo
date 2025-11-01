package com.example.scaneia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
public class UsuarioPerfilResponse {
    private String nome;

    private String email;

    @SerializedName("dataCriacao")
    @Expose
    private Date dataCriacao;

    private String cpf;

    public UsuarioPerfilResponse() {

    }

    public UsuarioPerfilResponse(String nome, String email, Date dataCriacao, String cpf) {
        this.nome = nome;
        this.email = email;
        this.dataCriacao = dataCriacao;
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }
}
