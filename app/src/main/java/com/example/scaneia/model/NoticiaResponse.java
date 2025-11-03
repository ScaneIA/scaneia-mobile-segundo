package com.example.scaneia.model;

import java.util.Date;

public class NoticiaResponse {
    private String data_coleta;
    private String link;
    private String titulo;

    public String getLinkOriginal() {
        return linkOriginal;
    }

    public void setLinkOriginal(String linkOriginal) {
        this.linkOriginal = linkOriginal;
    }

    private String linkOriginal;


    private transient Date parsedDate;

    public Date getParsedDate() {
        return parsedDate;
    }

    public void setParsedDate(Date parsedDate) {
        this.parsedDate = parsedDate;
    }

    public NoticiaResponse() {

    }
    public NoticiaResponse(String dataColeta, String link, String titulo, String linkOriginal) {
        this.data_coleta = dataColeta;
        this.link = link;
        this.titulo = titulo;
        this.linkOriginal = linkOriginal;
    }
    public String getData_coleta() {
        return data_coleta;
    }

    public void setData_coleta(String data_coleta) {
        this.data_coleta = data_coleta;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}