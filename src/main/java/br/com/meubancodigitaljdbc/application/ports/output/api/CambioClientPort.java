package br.com.meubancodigitaljdbc.application.ports.output.api;

import java.util.Map;

public interface CambioClientPort {

    Map<String, Object> buscarDadosDeCambio(String url) throws Exception;
}
