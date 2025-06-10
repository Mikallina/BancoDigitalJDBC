package br.com.meubancodigitaljdbc.application.service;

import br.com.meubancodigitaljdbc.application.domain.model.Endereco;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CepService {

    private final RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClienteService.class);

    @Autowired
    public CepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Cacheable("enderecos")
    public Endereco buscarEnderecoPorCep(String cep) {
        LOGGER.info("Chamando o ViaCEP..." + cep);
        String url = "https://viacep.com.br/ws/" + cep + "/json/";
        return restTemplate.getForObject(url, Endereco.class);
    }
}

