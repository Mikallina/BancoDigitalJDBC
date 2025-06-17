package br.com.meubancodigitaljdbc.application.ports.input.usecases;

import java.util.Map;

public interface CambioUseCase {

    double obterCotacao(String moedaBase, String moedaDestino)throws Exception;

    double converterMoeda (double valor, String moedaBase, String moedaDestino)throws Exception;

    Map<String, String> obterMoedasDisponiveis() throws Exception;

}
