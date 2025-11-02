package com.example.scaneia.model;

public class UserInfo {
    private int id;
    private String username;
    private String usuario_tipo;

    public UserInfo(int id, String username, String usuario_tipo) {
        this.id = id;
        this.username = username;
        this.usuario_tipo = usuario_tipo;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getUsuario_tipo() {
        return usuario_tipo;
    }
}
