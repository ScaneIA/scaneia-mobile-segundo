package com.example.scaneia.model;
public class CadastroRequest {
    private String email;
    private String senha;

    public CadastroRequest(String senha, String email) {
        this.senha = senha;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
