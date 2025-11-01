package com.example.scaneia.model;

import java.util.List;

public class FiltroInformacoesModelos {
    private String titulo;
    private Integer estrutura;
    private Integer numeroRegistros;
    private List<String> colunas;

    public FiltroInformacoesModelos(String titulo, List<String> colunas, Integer numeroRegistros, Integer estrutura) {
        this.titulo = titulo;
        this.colunas = colunas;
        this.numeroRegistros = numeroRegistros;
        this.estrutura = estrutura;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<String> getColunas() {
        return colunas;
    }

    public void setColunas(List<String> colunas) {
        this.colunas = colunas;
    }

    public Integer getEstrutura() {
        return estrutura;
    }

    public void setEstrutura(Integer estrutura) {
        this.estrutura = estrutura;
    }

    public Integer getNumeroRegistros() {
        return numeroRegistros;
    }

    public void setNumeroRegistros(Integer numeroRegistros) {
        this.numeroRegistros = numeroRegistros;
    }
}
