package br.com.meubancodigitaljdbc.adapters.input.controllers;

import br.com.meubancodigitaljdbc.adapters.input.controllers.mapper.ClienteMapper;
import br.com.meubancodigitaljdbc.adapters.input.controllers.mapper.ContaMapper;
import br.com.meubancodigitaljdbc.adapters.input.controllers.request.ContaRequest;
import br.com.meubancodigitaljdbc.adapters.input.controllers.request.DepositoRequest;
import br.com.meubancodigitaljdbc.adapters.input.controllers.request.TransferenciaRequest;
import br.com.meubancodigitaljdbc.adapters.input.controllers.response.ClienteResponse;
import br.com.meubancodigitaljdbc.adapters.input.controllers.response.ContaResponse;
import br.com.meubancodigitaljdbc.adapters.input.controllers.response.OperacaoResponse;
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
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Conta", description = "Operações bancárias relacionadas a contas")
@RestController
@RequestMapping("/conta")
public class ContaController {


    private static final Logger LOGGER = LoggerFactory.getLogger(ContaController.class);
    private static final String LOG_TEMPO_DECORRIDO = "Tempo Decorrido: {} milissegundos: {}";
    private final ContaUseCase contaUseCase;
    private final ClienteUseCase clienteUseCase;

    private final ContaMapper contaMapper;

    private final ClienteMapper clienteMapper;

    @Autowired
    public ContaController(ContaService contaUseCase, ClienteService clienteUserCase, ContaMapper contaMapper, ClienteMapper clienteMapper) {
        this.contaUseCase = contaUseCase;
        this.clienteUseCase = clienteUserCase;
        this.contaMapper = contaMapper;
        this.clienteMapper = clienteMapper;
    }

    @Operation(summary = "Criar uma nova Conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")

    })

    @PostMapping("/criarConta")
    public ResponseEntity<ContaResponse> criarConta(@RequestBody ContaRequest contaRequest,
                                                    HttpServletRequest httpRequest)
            throws SQLException {

        long tempoInicio = System.currentTimeMillis();
        Cliente cliente = clienteUseCase.buscarClientePorCpf(contaRequest.getCpf());
        Conta conta = contaUseCase.criarConta(cliente, contaRequest.getAgencia(), contaRequest.getTipoConta());

        ContaResponse response = contaMapper.contaToResponse(conta);

        LOGGER.info("Conta criada com sucesso para CPF {}", contaRequest.getCpf());
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, httpRequest.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar Conta por CPF")
    @GetMapping("/buscarConta/{cpf}")
    public ResponseEntity<ClienteResponse> buscarClienteComContas(@Parameter(description = "CPF do Cliente") @PathVariable String cpf, HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        Cliente cliente = clienteUseCase.buscarClientePorCpf(cpf);
        List<Conta> contas = contaUseCase.buscarContasPorCliente(cliente);
        cliente.setContas(contas);

        ClienteResponse response = clienteMapper.toResponse(cliente);

        LOGGER.info("Cliente encontrado: {} ", cpf);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());
        return ResponseEntity.ok(response);


    }

    @Operation(summary = "Realiza depósito em uma conta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Depósito realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/depositar")
    public ResponseEntity<OperacaoResponse> depositar(
            @RequestBody DepositoRequest depositoRequest,
            HttpServletRequest request
    ) throws SQLException, OperacoesException, ContaNaoValidaException {
        long tempoInicio = System.currentTimeMillis();

        contaUseCase.realizarDeposito(depositoRequest.getNumContaDestino(), depositoRequest.getValor());

        LOGGER.info("Depósito realizado na conta {} no valor de R$ {}", depositoRequest.getNumContaDestino(), depositoRequest.getValor());
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        OperacaoResponse response = new OperacaoResponse(
                "Deposito realizado com Sucesso",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Efetuar uma transferência via PIX",
            description = "Realiza uma transferência instantânea entre contas utilizando chave PIX."
    )
    @ApiResponse(responseCode = "200", description = "PIX realizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou chave PIX incorreta")
    @PostMapping("/efetuarPIX")
    public ResponseEntity<OperacaoResponse> eftuarPix(
            @RequestBody TransferenciaRequest transferenciaRequest,
            HttpServletRequest request
    ) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        contaUseCase.realizarTransferenciaPIX(
                transferenciaRequest.getValor(),
                transferenciaRequest.getNumContaOrigem(),
                transferenciaRequest.getChave()
        );

        LOGGER.info("PIX realizado com sucesso");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());
        OperacaoResponse response = new OperacaoResponse(
                "Pix Realizado com Sucesso",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Transferir para conta poupança",
            description = "Realiza uma transferência de uma conta corrente para uma conta poupança."
    )
    @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou conta não encontrada")
    @PostMapping("/transferirPoupanca")
    public ResponseEntity<OperacaoResponse> transferirPoupanca(
            @RequestBody TransferenciaRequest transferenciaRequest,
            HttpServletRequest request
    ) throws SQLException, ContaNaoValidaException {
        long tempoInicio = System.currentTimeMillis();

        contaUseCase.realizarTransferenciaPoupanca(
                transferenciaRequest.getValor(),
                transferenciaRequest.getNumContaOrigem(),
                transferenciaRequest.getNumContaDestino()
        );

        LOGGER.info("Transferência realizada com sucesso R$ {}", transferenciaRequest.getValor());

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());
        OperacaoResponse response = new OperacaoResponse(
                "Rendimento Aplicado com Sucesso",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Transferir para outras contas cadastradas",
            description = "Realiza a transferência entre contas correntes/poupança já registradas no banco."
    )
    @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou conta não encontrada")
    @PostMapping("/transferirOutrasContas")
    public ResponseEntity<OperacaoResponse> transferirOutrasContas(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da transferência entre contas",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TransferenciaRequest.class))
            )
            TransferenciaRequest transferenciaRequest,
            HttpServletRequest request
    ) throws SQLException, ContaNaoValidaException {
        long tempoInicio = System.currentTimeMillis();

        contaUseCase.realizarTransferenciaOutrasContas(
                transferenciaRequest.getValor(),
                transferenciaRequest.getNumContaDestino(),
                transferenciaRequest.getNumContaOrigem()
        );

        LOGGER.info("Transferência realizada com sucesso.");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());
        OperacaoResponse response = new OperacaoResponse(
                "Transferencia realizada com Sucesso",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Aplicar Manutenção de Conta")
    @ApiResponse(responseCode = "200", description = "Manutenção realizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro ao aplicar a taxa")
    @PutMapping("/{idConta}/manutencao")
    public ResponseEntity<OperacaoResponse> aplicarTaxaManutencao(
            @PathVariable Long idConta,
            HttpServletRequest request
    ) throws Exception {
        long tempoInicio = System.currentTimeMillis();

        contaUseCase.aplicarTaxaOuRendimento(idConta, TipoConta.CORRENTE, true);

        LOGGER.info("Taxa de Manutenção Aplicada");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        OperacaoResponse response = new OperacaoResponse(
                "Taxa Aplicado com Sucesso",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Aplicar Rendimentos")
    @ApiResponse(responseCode = "200", description = "Rendimento aplicado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro ao aplicar rendimentos")
    @PutMapping("/{idConta}/rendimentos")
    public ResponseEntity<OperacaoResponse> aplicarRendimentos(@PathVariable Long idConta, HttpServletRequest request) throws Exception {
        long tempoInicio = System.currentTimeMillis();

        contaUseCase.aplicarTaxaOuRendimento(idConta, TipoConta.POUPANCA, false);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        LOGGER.info("Taxa de Rendimentos aplicado com Sucesso");
        OperacaoResponse response = new OperacaoResponse(
                "Rendimento Aplicado com Sucesso",
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);

    }

    @Operation(summary = "Exibir Saldo detalhado")
    @GetMapping("/exibirSaldoDetalhado")
    public ResponseEntity<ContaResponse> exibirSaldoDetalhado(@RequestParam String cpf, @RequestParam String numConta, HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        Conta conta = contaUseCase.buscarContaPorClienteEConta(cpf, numConta);

        ContaResponse response = contaMapper.contaToResponse(conta);
        LOGGER.info("Exibir detalhes da conta...");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(response);

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
    public ResponseEntity<OperacaoResponse> deletarContaId(@PathVariable Long contaId, HttpServletRequest request) throws ClienteInvalidoException {
        long tempoInicio = System.currentTimeMillis();

        contaUseCase.deletarConta(contaId);

        LOGGER.info("Deletar Conta {}", contaId);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        OperacaoResponse response = new OperacaoResponse(
                "Conta deletada com sucesso.",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }


}
