package com.example.scaneia.model;

import androidx.navigation.fragment.DialogFragmentNavigator;

public class UsuarioHierarquia {

    Long idUsuarioTipo;
    Long idUsuario;
    String nomeUsuario;
    String descricaoUsuarioTipo;

    public UsuarioHierarquia(Long idUsuario, String nomeUsuario, Long idUsuarioTipo, String descricaoUsuarioTipo) {
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.idUsuarioTipo = idUsuarioTipo;
        this.descricaoUsuarioTipo = descricaoUsuarioTipo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public Long getIdUsuarioTipo() {
        return idUsuarioTipo;
    }

    public String getDescricaoUsuarioTipo() {
        return descricaoUsuarioTipo;
    }

    public void setDescricaoUsuarioTipo(String descricaoUsuarioTipo) {
        this.descricaoUsuarioTipo = descricaoUsuarioTipo;
    }

    public void setIdUsuarioTipo(Long idUsuarioTipo) {
        this.idUsuarioTipo = idUsuarioTipo;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
}
