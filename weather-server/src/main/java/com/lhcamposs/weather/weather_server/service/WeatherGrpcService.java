package com.lhcamposs.weather.weather_server.service;

import com.lhcamposs.weather.grpc.*;
import com.lhcamposs.weather.weather_server.data.WeatherDataStore;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@GrpcService
public class WeatherGrpcService extends WeatherServiceGrpc.WeatherServiceImplBase {

    @Autowired
    private WeatherDataStore dataStore;

    private final String[] DESCRICOES = {"Ensolarado", "Parcialmente Nublado", "Nublado", "Chuvoso", "Tempestuoso"};
    private final Random random = new Random();

    @Override
    public void obterTemperaturaAtual(CidadeRequest request,
                                      StreamObserver<TemperaturaResponse> responseObserver) {
        String cidade = request.getCidade();

        if (!dataStore.existeCidade(cidade)) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Cidade '" + cidade + "' nao encontrada.")
                    .asRuntimeException());
            return;
        }

        List<Double> temps = dataStore.getTemperaturas(cidade);
        double tempAtual = temps.get(random.nextInt(temps.size()));

        TemperaturaResponse response = TemperaturaResponse.newBuilder()
                .setCidade(cidade)
                .setTemperatura(tempAtual)
                .setDescricao(DESCRICOES[random.nextInt(DESCRICOES.length)])
                .setUnidade("Celsius")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void previsaoCincoDias(CidadeRequest request,
                                  StreamObserver<PrevisaoResponse> responseObserver) {
        String cidade = request.getCidade();

        if (!dataStore.existeCidade(cidade)) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Cidade '" + cidade + "' nao encontrada.")
                    .asRuntimeException());
            return;
        }

        List<Double> temps = dataStore.getTemperaturas(cidade);
        PrevisaoResponse.Builder builder = PrevisaoResponse.newBuilder().setCidade(cidade);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 0; i < 5; i++) {
            double base = temps.get(i % temps.size());
            builder.addDias(PrevisaoDia.newBuilder()
                    .setData(LocalDate.now().plusDays(i + 1).format(fmt))
                    .setTempMin(base - 3)
                    .setTempMax(base + 3)
                    .setDescricao(DESCRICOES[random.nextInt(DESCRICOES.length)])
                    .build());
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void listarCidades(ListarCidadesRequest request,
                              StreamObserver<ListarCidadesResponse> responseObserver) {
        ListarCidadesResponse response = ListarCidadesResponse.newBuilder()
                .addAllCidades(dataStore.getDados().keySet())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void cadastrarCidade(CadastrarCidadeRequest request,
                                StreamObserver<CadastrarCidadeResponse> responseObserver) {
        String nome = request.getNome();

        if (dataStore.existeCidade(nome)) {
            responseObserver.onNext(CadastrarCidadeResponse.newBuilder()
                    .setSucesso(false)
                    .setMensagem("Cidade '" + nome + "' ja cadastrada.")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        dataStore.adicionarCidade(nome, request.getTemperaturaInicial());

        responseObserver.onNext(CadastrarCidadeResponse.newBuilder()
                .setSucesso(true)
                .setMensagem("Cidade '" + nome + "' cadastrada com sucesso!")
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void estatisticasClimaticas(CidadeRequest request,
                                       StreamObserver<EstatisticasResponse> responseObserver) {
        String cidade = request.getCidade();

        if (!dataStore.existeCidade(cidade)) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Cidade '" + cidade + "' nao encontrada.")
                    .asRuntimeException());
            return;
        }

        List<Double> temps = dataStore.getTemperaturas(cidade);
        double media = temps.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double min   = temps.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max   = temps.stream().mapToDouble(Double::doubleValue).max().orElse(0);

        responseObserver.onNext(EstatisticasResponse.newBuilder()
                .setCidade(cidade)
                .setMedia(Math.round(media * 10.0) / 10.0)
                .setMinima(min)
                .setMaxima(max)
                .setTotalRegistros(temps.size())
                .build());
        responseObserver.onCompleted();
    }
}