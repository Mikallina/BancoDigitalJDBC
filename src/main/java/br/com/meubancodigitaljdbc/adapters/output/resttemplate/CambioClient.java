package br.com.meubancodigitaljdbc.adapters.output.resttemplate;

import br.com.meubancodigitaljdbc.application.ports.output.api.CambioClientPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class CambioClient implements CambioClientPort {
    @Autowired
    private final RestTemplate restTemplate;

    public CambioClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> buscarDadosDeCambio(String url) {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }
}
