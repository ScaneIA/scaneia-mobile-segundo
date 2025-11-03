package com.example.scaneia.model;

public class UserInfo {
    private int id;
    private String username;
    private int id_tipo_usuario;

    public UserInfo(int id, String username, int id_tipo_usuario) {
        this.id = id;
        this.username = username;
        this.id_tipo_usuario = id_tipo_usuario;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getIdTipoUsuario() {
        return id_tipo_usuario;
    }
}
