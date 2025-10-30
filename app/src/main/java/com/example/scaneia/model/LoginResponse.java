package com.example.scaneia.model;

public class LoginResponse {

    //Atributos
    private String accessToken;
    private String refreshToken;

    //Construtor
    public LoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    //Getters
    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
