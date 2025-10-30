package com.example.scaneia.model;

public class LoginRequest {

    //Atributos
    private String username;
    private String password;

    //Construtor

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}