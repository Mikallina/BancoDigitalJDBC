package br.com.meubancodigitaljdbc.controller;

import br.com.meubancodigitaljdbc.model.Endereco;
import br.com.meubancodigitaljdbc.service.CepService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CepController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CepController.class);

    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/buscar-endereco/{cep}")
    public Endereco buscarEndereco(@PathVariable String cep, HttpServletRequest request) {
        long tempoInicio = System.currentTimeMillis();
        LOGGER.info("Busando CEP: {}", cep);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: {} millisegundos {} ",tempototal, request.getRequestURI());
        return cepService.buscarEnderecoPorCep(cep);



    }

}
