package com.example.scaneia.model;

public class UserInfo {
    private int id_usuario;
    private String username;
    private int id_tipo_usuario;

    public UserInfo(int id_usuario, String username, int id_tipo_usuario) {
        this.id_usuario = id_usuario;
        this.username = username;
        this.id_tipo_usuario = id_tipo_usuario;
    }

    public int getId() {
        return id_usuario;
    }

    public String getUsername() {
        return username;
    }

    public int getIdTipoUsuario() {
        return id_tipo_usuario;
    }
}
