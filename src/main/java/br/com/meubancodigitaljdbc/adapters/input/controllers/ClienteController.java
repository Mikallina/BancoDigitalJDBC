package br.com.meubancodigitaljdbc.adapters.input.controllers;

import br.com.meubancodigitaljdbc.adapters.input.controllers.mapper.ClienteMapper;
import br.com.meubancodigitaljdbc.adapters.input.controllers.request.ClienteRequest;
import br.com.meubancodigitaljdbc.adapters.input.controllers.response.ClienteResponse;
import br.com.meubancodigitaljdbc.application.domain.exceptions.ClienteInvalidoException;
import br.com.meubancodigitaljdbc.application.domain.model.Cliente;
import br.com.meubancodigitaljdbc.application.ports.input.usecases.ClienteUseCase;
import br.com.meubancodigitaljdbc.application.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteUseCase clienteUseCase;

    private final ClienteMapper clienteMapper;

    @Autowired
    public ClienteController(ClienteService clienteUserCase, ClienteMapper clienteMapper) {
        this.clienteUseCase = clienteUserCase;
        this.clienteMapper = clienteMapper;
    }

    private static final String LOG_TEMPO_DECORRIDO = "Tempo Decorrido: {} milissegundos: {}";

    private static final Logger LOGGER = LoggerFactory.getLogger(ClienteController.class);


    @PostMapping("/adicionar-clienteRequest")
    @Operation(
            summary = "Adicionar novo clienteRequest",
            description = "Adiciona um novo clienteRequest ao banco de dados. Retorna status 201 se criado com sucesso."
    )
    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    public ResponseEntity<String> addCliente(
            @RequestBody ClienteRequest clienteRequest,
            HttpServletRequest request
    ) throws Exception {
        long tempoInicio = System.currentTimeMillis();

        LOGGER.info("Cliente recebido no controller: cpf={}, nome={}", clienteRequest.getCpf(), clienteRequest.getNome());
        Cliente cliente = clienteMapper.toRequest(clienteRequest);
        clienteUseCase.salvarCliente(cliente, false);


        LOGGER.info("Adicionar clienteRequest: {} ", clienteRequest);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.status(201).body("Cliente criado com Sucesso");
    }

    @Operation(
            summary = "Buscar cliente por CPF",
            description = "Retorna os dados de um cliente com base no CPF informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = Cliente.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/buscarCpf/{cpf}")
    public ResponseEntity<ClienteResponse> buscarClientePorCpf(
            @Parameter(description = "CPF do cliente a ser buscado", required = true, example = "12345678900")
            @PathVariable String cpf,
            HttpServletRequest request
    ) {
        long tempoInicio = System.currentTimeMillis();

        Cliente cliente = clienteUseCase.buscarClientePorCpf(cpf);

        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        ClienteResponse clienteResponse = clienteMapper.toResponse(cliente);

        LOGGER.info("Buscar cliente: {} ", cliente);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(clienteResponse);
    }


    @Operation(summary = "Listar todos os clientes",
            description = "Retorna uma lista com todos os clientes cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Cliente.class)))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/clientes/listar-clientes")
    public ResponseEntity<List<ClienteResponse>> getAllClientes(HttpServletRequest request) {
        long tempoInicio = System.currentTimeMillis();

        List<Cliente> clientes = clienteUseCase.listarClientes();

        LOGGER.info("Clientes encontrados: {}", clientes.size());

        List<ClienteResponse> clientesResponse = clientes.stream()
                .map(clienteMapper::toResponse)
                .toList();

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(clientesResponse);
    }

    @Operation(
            summary = "Buscar cliente por ID",
            description = "Retorna os dados de um cliente com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = Cliente.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/cadastro-cliente/{clienteId}")
    public ResponseEntity<ClienteResponse> buscarClientePorID(@PathVariable Long clienteId, HttpServletRequest request) {
        long tempoInicio = System.currentTimeMillis();

        Optional<Cliente> clienteOptional = clienteUseCase.findById(clienteId);

        LOGGER.info("Buscar cliente por ID: {} ", clienteOptional);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return clienteOptional
                .map(cliente -> ResponseEntity.ok(clienteMapper.toResponse(cliente)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Atualizar Cliente",
            description = "Atualiza as informações de um cliente baseado no ID fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = Cliente.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PutMapping("/atualizar-cliente/{clienteId}")
    public ResponseEntity<?> atualizarClientePorID(
            @PathVariable Long clienteId,
            @RequestBody ClienteRequest clienteRequest,
            HttpServletRequest request
    ) throws Exception {

        Cliente cliente = clienteMapper.toRequest(clienteRequest);
        cliente.setIdCliente(clienteId);

        Cliente clienteAtualizado = clienteUseCase.atualizarCliente(clienteId, cliente);

        ClienteResponse response = clienteMapper.toResponse(clienteAtualizado);

        return ResponseEntity.ok(response);
    }



    @Operation(
            summary = "Deletar Cliente",
            description = "Deleta um cliente com base no ID fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Erro ao tentar deletar o cliente")
    })
    @DeleteMapping("/deletar-cliente/{clienteId}")
    public ResponseEntity<String> deletarClientePorID(@PathVariable Long clienteId, HttpServletRequest request) throws ClienteInvalidoException {
        long tempoInicio = System.currentTimeMillis();

        clienteUseCase.deletarCliente(clienteId);

        LOGGER.info("Deletar Cliente {}", clienteId);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Cliente deletado com sucesso.");
    }

}
