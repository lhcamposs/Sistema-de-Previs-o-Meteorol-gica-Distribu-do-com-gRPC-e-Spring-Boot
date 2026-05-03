package com.lhcamposs.weather.weather_client.controller;

import com.lhcamposs.weather.weather_client.dto.CadastrarCidadeDTO;
import com.lhcamposs.weather.weather_client.service.WeatherClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class WeatherController {

    @Autowired
    private WeatherClientService service;

    @PostMapping("/cidade")
    public ResponseEntity<?> cadastrarCidade(@RequestBody CadastrarCidadeDTO dto) {
        try {
            return ResponseEntity.ok(service.cadastrarCidade(dto.getNome(), dto.getTemperaturaInicial()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/cidades")
    public ResponseEntity<?> listarCidades() {
        return ResponseEntity.ok(service.listarCidades());
    }

    @GetMapping("/temperatura")
    public ResponseEntity<?> temperatura(@RequestParam String cidade) {
        try {
            return ResponseEntity.ok(service.obterTemperatura(cidade));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/previsao")
    public ResponseEntity<?> previsao(@RequestParam String cidade) {
        try {
            return ResponseEntity.ok(service.obterPrevisao(cidade));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<?> estatisticas(@RequestParam String cidade) {
        try {
            return ResponseEntity.ok(service.obterEstatisticas(cidade));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("erro", e.getMessage()));
        }
    }
}