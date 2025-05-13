package br.com.meubancodigitaljdbc.controller;

import br.com.meubancodigitaljdbc.execptions.ClienteInvalidoException;
import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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

    private final ClienteService clienteService;
    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    private static final String LOG_TEMPO_DECORRIDO = "Tempo Decorrido: {} milissegundos: {}";

    private static final Logger LOGGER = LoggerFactory.getLogger(ClienteController.class);


    @Operation(
            summary = "Adicionar novo cliente",
            description = "Adiciona um novo cliente ao banco de dados. Retorna status 201 se criado com sucesso."
    )
    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    @PostMapping("/adicionar-cliente")
    public ResponseEntity<String> addCliente(
            @RequestBody(
                    description = "Dados do cliente a ser criado",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Cliente.class))
            )
           Cliente cliente,
            HttpServletRequest request
    ) throws Exception {
        long tempoInicio = System.currentTimeMillis();

        clienteService.salvarCliente(cliente, false);

        LOGGER.info("Adicionar cliente: {} ", cliente);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.status(201).body("Cliente criado cmo Sucesso");
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
    public ResponseEntity<Cliente> buscarClientePorCpf(
            @Parameter(description = "CPF do cliente a ser buscado", required = true, example = "12345678900")
            @PathVariable String cpf,
            HttpServletRequest request
    ) {
        long tempoInicio = System.currentTimeMillis();

        Cliente cliente = clienteService.buscarClientePorCpf(cpf);

        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        LOGGER.info("Buscar cliente: {} ", cliente);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(cliente);
    }


    @Operation(summary = "Listar todos os clientes",
            description = "Retorna uma lista com todos os clientes cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Cliente.class)))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/clientes")
    public ResponseEntity<List<Cliente>> getAllClientes(HttpServletRequest request) {
        long tempoInicio = System.currentTimeMillis();

        List<Cliente> clientes = clienteService.listarClientes();

        LOGGER.info("Clientes encontrados: {}", clientes.size());

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(clientes);
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
    public ResponseEntity<Cliente> buscarClientePorID(@PathVariable Long clienteId, HttpServletRequest request) {
        long tempoInicio = System.currentTimeMillis();

        Optional<Cliente> clienteOptional = clienteService.findById(clienteId);

        LOGGER.info("Buscar cliente por ID: {} ", clienteOptional);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return clienteOptional
                .map(ResponseEntity::ok)
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
    public Optional<Cliente> atualizarClientePorID(@PathVariable Long clienteId, HttpServletRequest request) {
        long tempoInicio = System.currentTimeMillis();
        Optional<Cliente> clienteExistente = clienteService.findById(clienteId);

        LOGGER.info("Atualizar cliente por ID {}", clienteId);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return clienteExistente;
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

        clienteService.deletarCliente(clienteId);

        LOGGER.info("Deletar Cliente {}", clienteId);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Cliente deletado com sucesso.");
    }

}
