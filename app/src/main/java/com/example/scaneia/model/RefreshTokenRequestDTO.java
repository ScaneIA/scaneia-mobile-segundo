package com.example.scaneia.model;

public class RefreshTokenRequestDTO {

    //Atributos
    private String refreshToken;

    //Construtor
    public RefreshTokenRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    //Getters
    public String getRefreshToken() {
        return refreshToken;
    }

    //Setters
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
