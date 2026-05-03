package com.lhcamposs.weather.weather_server.service;

import com.lhcamposs.weather.weather_server.data.WeatherDataStore;
import com.lhcamposs.weather.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

// @GrpcService registra esta classe como um serviço gRPC no Spring
@GrpcService
public class WeatherGrpcService extends WeatherServiceGrpc.WeatherServiceImplBase {

    @Autowired
    private WeatherDataStore dataStore;

    private final String[] DESCRICOES = {"Ensolarado", "Parcialmente Nublado", "Nublado", "Chuvoso", "Tempestuoso"};
    private final Random random = new Random();

    // =============================================
    // RPC 1: ObterTemperaturaAtual
    // =============================================
    @Override
    public void obterTemperaturaAtual(CidadeRequest request,
                                      StreamObserver<TemperaturaResponse> responseObserver) {
        String cidade = request.getCidade();

        if (!dataStore.existeCidade(cidade)) {
            // Retorna erro gRPC se cidade não existe
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Cidade '" + cidade + "' não encontrada.")
                            .asRuntimeException()
            );
            return;
        }

        List<Double> temps = dataStore.getTemperaturas(cidade);
        double tempAtual = temps.get(random.nextInt(temps.size()));
        String descricao = DESCRICOES[random.nextInt(DESCRICOES.length)];

        TemperaturaResponse response = TemperaturaResponse.newBuilder()
                .setCidade(cidade)
                .setTemperatura(tempAtual)
                .setDescricao(descricao)
                .setUnidade("Celsius")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // =============================================
    // RPC 2: PrevisaoCincoDias
    // =============================================
    @Override
    public void previsaoCincoDias(CidadeRequest request,
                                  StreamObserver<PrevisaoResponse> responseObserver) {
        String cidade = request.getCidade();

        if (!dataStore.existeCidade(cidade)) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Cidade '" + cidade + "' não encontrada.")
                            .asRuntimeException()
            );
            return;
        }

        List<Double> temps = dataStore.getTemperaturas(cidade);
        PrevisaoResponse.Builder previsaoBuilder = PrevisaoResponse.newBuilder().setCidade(cidade);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 0; i < 5; i++) {
            String data = LocalDate.now().plusDays(i + 1).format(formatter);
            double tempBase = temps.get(i % temps.size());

            PrevisaoDia dia = PrevisaoDia.newBuilder()
                    .setData(data)
                    .setTempMin(tempBase - 3)
                    .setTempMax(tempBase + 3)
                    .setDescricao(DESCRICOES[random.nextInt(DESCRICOES.length)])
                    .build();

            previsaoBuilder.addDias(dia);
        }

        responseObserver.onNext(previsaoBuilder.build());
        responseObserver.onCompleted();
    }

    // =============================================
    // RPC 3: ListarCidades
    // =============================================
    @Override
    public void listarCidades(ListarCidadesRequest request,
                              StreamObserver<ListarCidadesResponse> responseObserver) {

        ListarCidadesResponse response = ListarCidadesResponse.newBuilder()
                .addAllCidades(dataStore.getDados().keySet())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // =============================================
    // RPC 4: CadastrarCidade
    // =============================================
    @Override
    public void cadastrarCidade(CadastrarCidadeRequest request,
                                StreamObserver<CadastrarCidadeResponse> responseObserver) {
        String nome = request.getNome();
        double tempInicial = request.getTemperaturaInicial();

        if (dataStore.existeCidade(nome)) {
            CadastrarCidadeResponse response = CadastrarCidadeResponse.newBuilder()
                    .setSucesso(false)
                    .setMensagem("Cidade '" + nome + "' já está cadastrada.")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        dataStore.adicionarCidade(nome, tempInicial);

        CadastrarCidadeResponse response = CadastrarCidadeResponse.newBuilder()
                .setSucesso(true)
                .setMensagem("Cidade '" + nome + "' cadastrada com sucesso!")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // =============================================
    // RPC 5: EstatisticasClimaticas
    // =============================================
    @Override
    public void estatisticasClimaticas(CidadeRequest request,
                                       StreamObserver<EstatisticasResponse> responseObserver) {
        String cidade = request.getCidade();

        if (!dataStore.existeCidade(cidade)) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Cidade '" + cidade + "' não encontrada.")
                            .asRuntimeException()
            );
            return;
        }

        List<Double> temps = dataStore.getTemperaturas(cidade);

        double soma = temps.stream().mapToDouble(Double::doubleValue).sum();
        double media = soma / temps.size();
        double min = temps.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = temps.stream().mapToDouble(Double::doubleValue).max().orElse(0);

        EstatisticasResponse response = EstatisticasResponse.newBuilder()
                .setCidade(cidade)
                .setMedia(Math.round(media * 10.0) / 10.0)
                .setMinima(min)
                .setMaxima(max)
                .setTotalRegistros(temps.size())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
