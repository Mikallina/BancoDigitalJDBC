package br.com.meubancodigitaljdbc.controller;

import br.com.meubancodigitaljdbc.service.CambioService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@RestController
@RequestMapping("/cambio")
public class CambioController {

    @Autowired
    private CambioService cambioService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CambioController.class);
    @GetMapping("/converter")
    public ResponseEntity<?> converter(
            @RequestParam double valor,
            @RequestParam String moedaBase,
            @RequestParam String moedaDestino, HttpServletRequest request) throws Exception {

        long tempoInicio = System.currentTimeMillis();

        double valorConvertido = cambioService.converterMoeda(valor, moedaBase, moedaDestino);
        LOGGER.info("Conversão de moedas: Moeda Base:  {} Moeda destino: {} ", moedaBase, moedaDestino);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: {} milissegundos: {}", tempototal, request.getRequestURI());


        return ResponseEntity.ok(valorConvertido);
    }

    @GetMapping("/moedas")
    public ResponseEntity<?> listarMoedas() throws Exception {
        Map<String, String> moedas = cambioService.obterMoedasDisponiveis();
        LOGGER.info("Moedas" + moedas);
        return ResponseEntity.ok(moedas);
    }

}