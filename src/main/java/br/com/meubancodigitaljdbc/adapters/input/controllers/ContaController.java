package br.com.meubancodigitaljdbc.adapters.input.controllers;

import br.com.meubancodigitaljdbc.adapters.input.controllers.response.ContaResponse;
import br.com.meubancodigitaljdbc.adapters.input.controllers.request.DepositoRequest;
import br.com.meubancodigitaljdbc.adapters.input.controllers.request.TransferenciaRequest;
import br.com.meubancodigitaljdbc.application.domain.enuns.TipoConta;
import br.com.meubancodigitaljdbc.application.domain.exceptions.ClienteInvalidoException;
import br.com.meubancodigitaljdbc.application.domain.exceptions.ContaNaoValidaException;
import br.com.meubancodigitaljdbc.application.domain.exceptions.OperacoesException;
import br.com.meubancodigitaljdbc.application.domain.model.Cliente;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;
import br.com.meubancodigitaljdbc.application.ports.input.usecases.ClienteUseCase;
import br.com.meubancodigitaljdbc.application.ports.input.usecases.ContaUseCase;
import br.com.meubancodigitaljdbc.application.service.ClienteService;
import br.com.meubancodigitaljdbc.application.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Tag(name = "Conta", description = "Operações bancárias relacionadas a contas")
@RestController
@RequestMapping("/conta")
public class ContaController {


    private static final Logger LOGGER = LoggerFactory.getLogger(ContaController.class);
    private static final String LOG_TEMPO_DECORRIDO = "Tempo Decorrido: {} milissegundos: {}";
    private final ContaUseCase contaService;
    private final ClienteUseCase clienteUseCase;

    @Autowired
    public ContaController(ContaService contaService, ClienteService clienteUserCase) {
        this.contaService = contaService;
        this.clienteUseCase = clienteUserCase;
    }

    @Operation(summary = "Criar uma nova Conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")

    })

    @PostMapping("/criarConta")
    public ResponseEntity<Conta> criarConta(@Parameter(description = "CPF do Cliente") @RequestParam String cpf, @Parameter(description = "Agência do Cliente") @RequestParam int agencia,
                                            @Parameter(description = "Tipo de Conta do Cliente") @RequestParam TipoConta tipoConta, HttpServletRequest request)
            throws SQLException {

        long tempoInicio = System.currentTimeMillis();
        Cliente cliente = clienteUseCase.buscarClientePorCpf(cpf);
        Conta conta = contaService.criarConta(cliente, agencia, tipoConta);

        LOGGER.info("Conta criada com sucesso para CPF {}", cpf);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(conta);
    }

    @Operation(summary = "Buscar Conta por CPF")
    @GetMapping("/buscarConta/{cpf}")
    public ResponseEntity<Cliente> buscarClienteComContas(@Parameter(description = "CPF do Cliente") @PathVariable String cpf, HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();
        Cliente cliente = clienteUseCase.buscarClientePorCpf(cpf);

        List<Conta> contas = contaService.buscarContasPorCliente(cliente);
        cliente.setContas(contas);

        LOGGER.info("Cliente encontrado: {} ", cpf);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());
        return ResponseEntity.ok(cliente);


    }

    @Operation(summary = "Realiza depósito em uma conta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Depósito realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/depositar")
    public ResponseEntity<String> depositar(
            @RequestBody DepositoRequest depositoRequest,
            HttpServletRequest request
    ) throws SQLException, OperacoesException, ContaNaoValidaException {
        long tempoInicio = System.currentTimeMillis();

        contaService.realizarDeposito(depositoRequest.getNumContaDestino(), depositoRequest.getValor());

        LOGGER.info("Depósito realizado na conta {} no valor de R$ {}", depositoRequest.getNumContaDestino(), depositoRequest.getValor());
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Depósito realizado com sucesso.");
    }

    @Operation(
            summary = "Efetuar uma transferência via PIX",
            description = "Realiza uma transferência instantânea entre contas utilizando chave PIX."
    )
    @ApiResponse(responseCode = "200", description = "PIX realizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou chave PIX incorreta")
    @PostMapping("/efetuarPIX")
    public ResponseEntity<String> eftuarPix(
            @RequestBody TransferenciaRequest transferenciaRequest,
            HttpServletRequest request
    ) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        contaService.realizarTransferenciaPIX(
                transferenciaRequest.getValor(),
                transferenciaRequest.getNumContaOrigem(),
                transferenciaRequest.getChave()
        );

        LOGGER.info("PIX realizado com sucesso");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("PIX realizado com sucesso");
    }


    @Operation(
            summary = "Transferir para conta poupança",
            description = "Realiza uma transferência de uma conta corrente para uma conta poupança."
    )
    @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou conta não encontrada")
    @PostMapping("/transferirPoupanca")
    public ResponseEntity<String> transferirPoupanca(
            @RequestBody TransferenciaRequest transferenciaRequest,
            HttpServletRequest request
    ) throws SQLException, ContaNaoValidaException {
        long tempoInicio = System.currentTimeMillis();

        contaService.realizarTransferenciaPoupanca(
                transferenciaRequest.getValor(),
                transferenciaRequest.getNumContaOrigem(),
                transferenciaRequest.getNumContaDestino()
        );

        LOGGER.info("Transferência realizada com sucesso R$ {}", transferenciaRequest.getValor());

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Transferência para conta poupança realizada com sucesso!");
    }


    @Operation(
            summary = "Transferir para outras contas cadastradas",
            description = "Realiza a transferência entre contas correntes/poupança já registradas no banco."
    )
    @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou conta não encontrada")
    @PostMapping("/transferirOutrasContas")
    public ResponseEntity<String> transferirOutrasContas(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da transferência entre contas",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TransferenciaRequest.class))
            )
            TransferenciaRequest transferenciaRequest,
            HttpServletRequest request
    ) throws SQLException, ContaNaoValidaException {
        long tempoInicio = System.currentTimeMillis();

        contaService.realizarTransferenciaOutrasContas(
                transferenciaRequest.getValor(),
                transferenciaRequest.getNumContaDestino(),
                transferenciaRequest.getNumContaOrigem()
        );

        LOGGER.info("Transferência realizada com sucesso.");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Transferência realizada com sucesso!");
    }


    @Operation(summary = "Aplicar Manutenção de Conta")
    @ApiResponse(responseCode = "200", description = "Manutenção realizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro ao aplicar a taxa")
    @PutMapping("/{idConta}/manutencao")
    public ResponseEntity<String> aplicarTaxaManutencao(
            @PathVariable Long idConta,
            HttpServletRequest request
    ) throws Exception {
        long tempoInicio = System.currentTimeMillis();

        contaService.aplicarTaxaOuRendimento(idConta, TipoConta.CORRENTE, true);

        LOGGER.info("Taxa de Manutenção Aplicada");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Taxa de Manutenção aplicada com sucesso");
    }


    @Operation(summary = "Aplicar Rendimentos")
    @ApiResponse(responseCode = "200", description = "Rendimento aplicado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro ao aplicar rendimentos")
    @PutMapping("/{idConta}/rendimentos")
    public ResponseEntity<String> aplicarRendimentos(@PathVariable Long idConta, HttpServletRequest request) throws Exception {
        long tempoInicio = System.currentTimeMillis();

        contaService.aplicarTaxaOuRendimento(idConta, TipoConta.POUPANCA, false);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        LOGGER.info("Taxa de Rendimentos aplicado com Sucesso");
        return ResponseEntity.ok("Rendimento aplicado com sucesso");

    }

    @Operation(summary = "Exibir Saldo detalhado")
    @GetMapping("/exibirSaldoDetalhado")
    public ResponseEntity<ContaResponse> exibirSaldoDetalhado(@RequestParam String cpf, @RequestParam String numConta, HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        Conta conta = contaService.buscarContaPorClienteEConta(cpf, numConta);

        ContaResponse contaResponse = new ContaResponse(conta.getCliente().getNome(),
                conta.getCliente().getCpf(), conta);

        LOGGER.info("Exibir detalhes da conta...");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(contaResponse);

    }

    @Operation(
            summary = "Deletar Conta",
            description = "Deleta um cliente com base no ID fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrado"),
            @ApiResponse(responseCode = "400", description = "Erro ao tentar deletar a conta")
    })
    @DeleteMapping("/deletar-conta/{contaId}")
    public ResponseEntity<String> deletarContaId(@PathVariable Long contaId, HttpServletRequest request) throws ClienteInvalidoException {
        long tempoInicio = System.currentTimeMillis();

        contaService.deletarConta(contaId);

        LOGGER.info("Deletar Conta {}", contaId);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Conta deletada com sucesso.");
    }


}
