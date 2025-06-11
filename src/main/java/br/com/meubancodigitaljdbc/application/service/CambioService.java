package br.com.meubancodigitaljdbc.application.service;

import br.com.meubancodigitaljdbc.application.ports.input.usecases.CambioUserCase;
import br.com.meubancodigitaljdbc.adapters.output.resttemplate.CambioClient;
import br.com.meubancodigitaljdbc.application.domain.exceptions.CambioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CambioService implements CambioUserCase {


    @Value("${api.cambio.url}")
    private String apiUrl;

    @Value("${api.cambio.key}")
    private String apiKey;


    private final CambioClient cambioClient;

    private static final String CONVERSION_RATES_KEY = "conversion_rates";
    @Autowired
    public CambioService(CambioClient cambioClient) {
        this.cambioClient = cambioClient;
    }


    public double obterCotacao(String moedaBase, String moedaDestino) throws Exception {
        try {

            String url = String.format("%s/%s/latest/%s", apiUrl, apiKey, moedaBase);

            // Requisição para obter a resposta da API
            Map<String, Object> body = cambioClient.buscarDadosDeCambio(url);

            // Verifique se a resposta contém a chave "conversion_rates"
            if (body == null || !body.containsKey(CONVERSION_RATES_KEY)) {
                throw new CambioException("A resposta da API não contém a chave'" + CONVERSION_RATES_KEY + "'.");
            }

            // Acessar o mapa de taxas de câmbio (conversion_rates)
            Map<String, Double> conversionRates = (Map<String, Double>) body.get(CONVERSION_RATES_KEY);

            // Verifique se a moeda de destino existe no mapa de taxas
            Double cotacao = conversionRates.get(moedaDestino);

            if (cotacao == null) {
                throw new CambioException("Moeda de destino não encontrada.");
            }

            return cotacao;

        } catch (Exception e) {
            throw new CambioException("Erro ao buscar cotação: " + e.getMessage());
        }
    }


    // Método para converter o valor
    public double converterMoeda(double valor, String moedaBase, String moedaDestino) throws Exception {
        double cotacao = obterCotacao(moedaBase, moedaDestino);
        return valor * cotacao;
    }

    public Map<String, String> obterMoedasDisponiveis() throws Exception {
        String url = String.format("%s/%s/latest/USD", apiUrl, apiKey);

        Map<String, Object> body = cambioClient.buscarDadosDeCambio(url);

        if (body == null || !body.containsKey(CONVERSION_RATES_KEY)) {
            throw new CambioException("A resposta da API não contém a chave '" + CONVERSION_RATES_KEY + "'.");
        }

        try {
            return (Map<String, String>) body.get(CONVERSION_RATES_KEY);
        } catch (ClassCastException e) {
            throw new CambioException("Erro ao interpretar os dados da API.");
        }
    }



}
