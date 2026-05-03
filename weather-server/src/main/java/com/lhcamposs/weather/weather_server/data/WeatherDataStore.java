package com.lhcamposs.weather.weather_server.data;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WeatherDataStore {

    private final Map<String, List<Double>> dadosCidades = new HashMap<>();

    public WeatherDataStore() {
        dadosCidades.put("Urutaí",    Arrays.asList(28.0, 30.0, 25.0, 32.0, 27.0));
        dadosCidades.put("Goiânia",   Arrays.asList(33.0, 35.0, 31.0, 34.0, 29.0));
        dadosCidades.put("Brasília",  Arrays.asList(26.0, 28.0, 24.0, 30.0, 25.0));
        dadosCidades.put("São Paulo", Arrays.asList(22.0, 24.0, 19.0, 25.0, 20.0));
        dadosCidades.put("Fortaleza", Arrays.asList(31.0, 33.0, 30.0, 34.0, 32.0));
    }

    public Map< String, List<Double>> getDados() {
        return dadosCidades;
    }

    public boolean existeCidade(String nome) {
        return dadosCidades.containsKey(nome);
    }

    public void adicionarCidade(String nome, double temperaturaInicial) {
        List<Double> temps = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < 5; i++ ) {
            temps.add(temperaturaInicial + (rand.nextDouble() * 10 - 5));
        }
        dadosCidades.put(nome, temps);
    }

    public List<Double> getTemperaturas(String cidade) {
        return dadosCidades.getOrDefault(cidade, Collections.emptyList());
    }
}
