package com.lhcamposs.weather.weather_client.dto;

public class CadastrarCidadeDTO {
    private String nome;
    private double temperaturaInicial;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public double getTemperaturaInicial() { return temperaturaInicial; }
    public void setTemperaturaInicial(double t) { this.temperaturaInicial = t; }
}