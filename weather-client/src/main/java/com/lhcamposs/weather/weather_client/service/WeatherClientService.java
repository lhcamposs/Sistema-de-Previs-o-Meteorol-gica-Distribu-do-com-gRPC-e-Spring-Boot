package com.lhcamposs.weather.weather_client.service;

import com.lhcamposs.weather.grpc.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WeatherClientService {

    @GrpcClient("weather-server")
    private WeatherServiceGrpc.WeatherServiceBlockingStub weatherStub;

    public Map<String, Object> obterTemperatura(String cidade) {
        try {
            CidadeRequest req = CidadeRequest.newBuilder().setCidade(cidade).build();
            TemperaturaResponse res = weatherStub.obterTemperaturaAtual(req);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("cidade", res.getCidade());
            result.put("temperatura", res.getTemperatura());
            result.put("descricao", res.getDescricao());
            result.put("unidade", res.getUnidade());
            return result;
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Erro gRPC: " + e.getStatus().getDescription());
        }
    }

    public Map<String, Object> obterPrevisao(String cidade) {
        try {
            CidadeRequest req = CidadeRequest.newBuilder().setCidade(cidade).build();
            PrevisaoResponse res = weatherStub.previsaoCincoDias(req);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("cidade", res.getCidade());
            result.put("previsao", res.getDiasList().stream().map(dia -> {
                Map<String, Object> d = new LinkedHashMap<>();
                d.put("data", dia.getData());
                d.put("temp_min", dia.getTempMin());
                d.put("temp_max", dia.getTempMax());
                d.put("descricao", dia.getDescricao());
                return d;
            }).toList());
            return result;
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Erro gRPC: " + e.getStatus().getDescription());
        }
    }

    public List<String> listarCidades() {
        ListarCidadesResponse res = weatherStub.listarCidades(
                ListarCidadesRequest.newBuilder().build());
        return res.getCidadesList();
    }

    public Map<String, Object> cadastrarCidade(String nome, double tempInicial) {
        CadastrarCidadeRequest req = CadastrarCidadeRequest.newBuilder()
                .setNome(nome)
                .setTemperaturaInicial(tempInicial)
                .build();
        CadastrarCidadeResponse res = weatherStub.cadastrarCidade(req);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sucesso", res.getSucesso());
        result.put("mensagem", res.getMensagem());
        return result;
    }

    public Map<String, Object> obterEstatisticas(String cidade) {
        try {
            CidadeRequest req = CidadeRequest.newBuilder().setCidade(cidade).build();
            EstatisticasResponse res = weatherStub.estatisticasClimaticas(req);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("cidade", res.getCidade());
            result.put("media", res.getMedia());
            result.put("minima", res.getMinima());
            result.put("maxima", res.getMaxima());
            result.put("total_registros", res.getTotalRegistros());
            return result;
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Erro gRPC: " + e.getStatus().getDescription());
        }
    }
}