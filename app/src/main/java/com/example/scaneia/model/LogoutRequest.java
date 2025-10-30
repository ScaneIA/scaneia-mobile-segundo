package com.example.scaneia.model;

public class LogoutRequest {

    //Atributos
    private String refreshToken;


    //Construtor
    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    //Getters e Setters

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }


}
