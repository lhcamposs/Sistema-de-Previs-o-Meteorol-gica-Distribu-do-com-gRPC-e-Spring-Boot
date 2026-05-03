package com.lhcamposs.weather.weather_client.dto;

public class PrevisaoDiaDTO {
    private String data;
    private double tempMin;
    private double tempMax;
    private String descricao;

    public PrevisaoDiaDTO(String data, double tempMin, double tempMax, String descricao) {
        this.data = data;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.descricao = descricao;
    }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public double getTempMin() { return tempMin; }
    public void setTempMin(double tempMin) { this.tempMin = tempMin; }

    public double getTempMax() { return tempMax; }
    public void setTempMax(double tempMax) { this.tempMax = tempMax; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}