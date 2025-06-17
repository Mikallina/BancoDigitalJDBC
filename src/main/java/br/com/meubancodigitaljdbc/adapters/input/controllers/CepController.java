package br.com.meubancodigitaljdbc.adapters.input.controllers;

import br.com.meubancodigitaljdbc.adapters.input.controllers.mapper.EnderecoMapper;
import br.com.meubancodigitaljdbc.adapters.input.controllers.response.EnderecoResponse;
import br.com.meubancodigitaljdbc.application.domain.model.Endereco;
import br.com.meubancodigitaljdbc.application.ports.input.usecases.CepUseCase;
import br.com.meubancodigitaljdbc.application.service.CepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cep")
public class CepController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CepController.class);

    private final CepUseCase cepUseCase;
    private final EnderecoMapper enderecoMapper;

    public CepController(CepService cepUserCase, EnderecoMapper enderecoMapper) {
        this.cepUseCase = cepUserCase;
        this.enderecoMapper = enderecoMapper;
    }

    @Operation(
            summary = "Buscar endereço por CEP",
            description = "Este endpoint permite buscar o endereço de um local com base no CEP informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = Endereco.class))),
            @ApiResponse(responseCode = "400", description = "CEP inválido ou mal formatado"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado para o CEP informado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/buscar-endereco/{cep}")
    public ResponseEntity<EnderecoResponse> buscarEndereco(@PathVariable String cep, HttpServletRequest request) {
        long tempoInicio = System.currentTimeMillis();
        LOGGER.info("Buscando CEP: {}", cep);

        Endereco endereco = cepUseCase.buscarEnderecoPorCep(cep);
        EnderecoResponse response = enderecoMapper.endereco(endereco);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: {} millisegundos {}", tempototal, request.getRequestURI());

        return ResponseEntity.ok(response);
    }


}
