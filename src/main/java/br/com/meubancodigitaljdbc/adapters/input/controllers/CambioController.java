package br.com.meubancodigitaljdbc.adapters.input.controllers;

import br.com.meubancodigitaljdbc.application.ports.input.usecases.CambioUseCase;
import br.com.meubancodigitaljdbc.application.service.CambioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    private final CambioUseCase cambioUseCase;
    @Autowired
    public CambioController(CambioService cambioService, CambioUseCase cambioUseCase) {
        this.cambioUseCase = cambioUseCase;

    }
    private static final Logger LOGGER = LoggerFactory.getLogger(CambioController.class);

    @Operation(
            summary = "Converter valor entre duas moedas",
            description = "Este endpoint permite converter um valor de uma moeda base para uma moeda destino especificada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversão realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos, como valores de moedas incorretos"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao realizar a conversão")
    })
    @GetMapping("/converter")
    public ResponseEntity<Double> converter(
            @RequestParam double valor,
            @RequestParam String moedaBase,
            @RequestParam String moedaDestino,
            HttpServletRequest request) throws Exception {

        long tempoInicio = System.currentTimeMillis();

        double valorConvertido = cambioUseCase.converterMoeda(valor, moedaBase, moedaDestino);

        LOGGER.info("Conversão de moedas: Moeda Base: {} Moeda Destino: {} ", moedaBase, moedaDestino);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: {} milissegundos {}", tempototal, request.getRequestURI());

        return ResponseEntity.ok(valorConvertido);
    }


    @Operation(
            summary = "Listar moedas disponíveis",
            description = "Este endpoint retorna uma lista das moedas disponíveis para conversão."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Moedas listadas com sucesso",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao listar as moedas")
    })
    @GetMapping("/moedas")
    public ResponseEntity<Map<String, String>> listarMoedas() throws Exception {
        Map<String, String> moedas = cambioUseCase.obterMoedasDisponiveis();

        LOGGER.info("Moedas: {}", moedas);

        return ResponseEntity.ok(moedas);
    }


}